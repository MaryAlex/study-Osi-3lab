package com.company;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Window extends JFrame {
    public static final String FILE_END_IN = ".IN";
    public static final String FILE_END_OUT = ".OUT";
    private List<TestData> allData = Collections.synchronizedList(new ArrayList<>());
    private List<Boolean> answers = Collections.synchronizedList(new ArrayList<>());
    private List<Boolean> toOutput = Collections.synchronizedList(new ArrayList<>());
    private Thread cheakerIn = new Thread(checkerIn());
    private Thread solver = new Thread(solver());
    private Thread cheakerOut = new Thread(cheakerOut());
    private List<Integer> n = Collections.synchronizedList(new ArrayList<>());
    private JPanel panel;
    private JButton startButton;
    private JButton stopButton;
    private JButton resumeButton;
    private Boolean stopSolver = false;
    private JTextArea textArea1;
    private String path = "";
    private String file = "";
    private List<String> files = Collections.synchronizedList(new ArrayList<>());
    private File f;

    public Window() {
        super("Hello");
        setContentPane(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        startButton.addActionListener(startWork());
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSolver = true;
            }
        });
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSolver = false;
            }
        });

        setVisible(true);
    }

    private ActionListener startWork() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                path = openL();
                if (path.equals("")) {
                    return;
                }
                f = new File(path);
                cheakerIn.run();
                solver.run();
                cheakerOut.run();
            }
        };
    }

    private Runnable solver() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 0;
                    while (true) {
                        while (allData.size() != 0) {
                            i++;
                            TestData tmp = new TestData(allData.get(0));
                            allData.remove(0);
                            answers.add(tmp.isBox());
                            toOutput.add(tmp.isBox());
                            while (stopSolver) {
                                Thread.sleep(1000);
                            }
                        }
                        if (i >= n.get(0)) {
                            n.remove(0);
                            break;
                        }
                    }
                    System.out.println("Solver done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Runnable cheakerOut() {
        return new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (files.size() != 0) {
                        textArea1.append(files.get(0) + "\n");
                        try (FileInputStream in = new FileInputStream(files.get(0) + FILE_END_OUT);
                             BufferedReader reader =
                                     new BufferedReader(new InputStreamReader(in))) {
                            File fileOutput = new File(files.get(0) + "programs.txt");
                            PrintWriter writer = new PrintWriter(fileOutput, "UTF-8");
                            String line = "";
                            int i = 0;
                            while ((line = reader.readLine()) != null) {
                                String answer = answers.get(0)? "YES" : "NO";
                                writer.print(i + ". " + answer + "\n");
                                writer.flush();
                                textArea1.append(i + ". " + answer + "\n");
                                if (!answer.equals(line)) {
                                    textArea1.append("Wrong answer in " + i + " test! It's wrong answer upper!\n");
                                    writer.print("Wrong answer in " + i + " test! It's wrong answer upper!\n");
                                    writer.flush();
                                }
                                answers.remove(0);
                                i++;
                                System.out.println(line + " line");
                            }
                            writer.close();
                            files.remove(0);
                        } catch (IOException x) {
                            System.err.println(x);
                        }
                    }
                    if (!cheakerIn.isAlive()) {
                        break;
                    }
                    System.out.println("checkerOut done!");
                }
            }
        };
    }

    private Runnable checkerIn() {
        return new Runnable() {
            @Override
            public void run() {
                outerloop:
                for (String fileName : f.list()) {
                    if (!fileName.endsWith(".IN")) {
                        continue;
                    }
                    file = path + File.separator + fileName.substring(0, fileName.indexOf("."));
                    try (FileInputStream in = new FileInputStream(file + FILE_END_IN);
                         BufferedReader reader =
                                 new BufferedReader(new InputStreamReader(in))) {
                        String line = null;
                        if ((line = reader.readLine()) != null) {
                            System.out.println(line + " line");
                        }
                        if (line == null || line.contains(" ")) {
                            System.out.println("Incorrect data");
                            n.add(0);
                            continue;
                        }
                        Integer nLocal = Integer.valueOf(line);
                        if (nLocal < 1 || nLocal > 5) {
                            System.out.println("Incorrect data");
                            nLocal = 0;
                            continue;
                        }
                        System.out.println(nLocal + " n");
                        for (int i = 0; i < nLocal; i++) {
                            String test = reader.readLine();
                            List<String> data = new ArrayList<>(Arrays.asList(test.split(" ")));
                            data.removeIf(s -> s.equals(""));
                            if (data.size() > 12) {
                                System.out.println("Incorrect data");
                                nLocal = 0;
                                continue outerloop;
                            }
                            List<BoxPanel> panels = new ArrayList<>();
                            for (int j = 0; j < 12; j += 2) {
                                if (Integer.valueOf(data.get(j)) > 10000 || Integer.valueOf(data.get(j + 1)) > 10000) {
                                    nLocal = 0;
                                    continue outerloop;
                                }
                                panels.add(new BoxPanel(Integer.valueOf(data.get(j)), Integer.valueOf(data.get(j + 1))));
                            }
                            allData.add(new TestData(panels));
                            System.out.println(i + " i in reading");
                        }
                        files.add(file);
                        n.add(nLocal);
                    } catch (IOException x) {
                        System.err.println(x);
                    }
                }
            }
        };

    }


//
//    public String saveL() {
//        String path = null;
//        JFileChooser c = new JFileChooser();
//        FileNameExtensionFilter filter = new FileNameExtensionFilter(FILE_END_IN, FILE_END_IN);
//        c.setFileFilter(filter);
//        // c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        int rVal = c.showSaveDialog(Window.this);
//        if (rVal == JFileChooser.APPROVE_OPTION) {
//            path = c.getSelectedFile().toString();
//            if (!path.endsWith("."+FILE_END_IN)) {
//                path += "." + FILE_END_IN;
//            }
//        }
//        if (rVal == JFileChooser.CANCEL_OPTION) {
//            /* no-op */
//        }
//        return path;
//    }
//
    public String openL() {
        String path = null;
        JFileChooser c = new JFileChooser();
//        FileNameExtensionFilter filter = new FileNameExtensionFilter(FILE_END_IN, FILE_END_IN);
//        c.setFileFilter(filter);
        c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int rVal = c.showOpenDialog(Window.this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            path = c.getSelectedFile().toString();
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
            /* no-op */
        }
        return path;
    }
}
