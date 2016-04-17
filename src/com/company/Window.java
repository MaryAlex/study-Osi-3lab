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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Window extends JFrame {
    public static final String FILE_END_IN = ".IN";
    public static final String FILE_END_OUT = ".OUT";
    private List<TestData> allData = new ArrayList<>();
    private List<Boolean> answers = new ArrayList<>();
    private List<Boolean> toOutput = new ArrayList<>();
    private Thread cheakerIn = new Thread(checkerIn());
    private Thread solver = new Thread(solver());
    private Thread cheakerOut = new Thread(cheakerOut());
    private Integer n;
    private JPanel panel;
    private JButton startButton;
    private JButton stopButton;
    private JButton resumeButton;
    private Boolean stopSolver = false;
    private JTextArea textArea1;
    private String path = "";
    private String file = "";

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
                File f = new File(path);
                for (String fileName : f.list()) {
                    if (!fileName.endsWith(".IN")) {
                        continue;
                    }
                    file = fileName.substring(0, fileName.indexOf("."));
                    cheakerIn.run();
                    solver.run();
                    cheakerOut.run();
                    try {
                        if (solver.isAlive()) {
                            solver.wait();
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        FileOutputStream out = new FileOutputStream("myAnswer" + file + FILE_END_OUT);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                        textArea1.setText(file);
                        for (int i = 0; i < toOutput.size(); i++) {
                            textArea1.append(i + ". " + toOutput.get(i) + "\n");
                            writer.write(toOutput.get(0)? "YES" : "NO");
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        if (cheakerOut.isAlive()) {
                            cheakerOut.wait();
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
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
                        if (i == n) {
                            break;
                        }
                    }
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
                try (FileInputStream in = new FileInputStream(file + FILE_END_OUT);
                     BufferedReader reader =
                             new BufferedReader(new InputStreamReader(in))) {
                    String line = null;
                    int i = 0;
                    while (true) {
                        while (answers.size() != 0) {
                            while ((line = reader.readLine()) != null) {
                                String answer = answers.get(0)? "YES" : "NO";
                                if (!answer.equals(line)) {
                                    System.out.println("Wrong answer!");
                                }
                                answers.remove(0);
                                i++;
                                System.out.println(line + " line");
                            }
                        }
                        if (i == n) {
                            break;
                        }
                    }
                } catch (IOException x) {
                    System.err.println(x);
                }
            }
        };
    }

    private Runnable checkerIn() {
        return new Runnable() {
            @Override
            public void run() {
                try (FileInputStream in = new FileInputStream(file + FILE_END_IN);
                     BufferedReader reader =
                             new BufferedReader(new InputStreamReader(in))) {
                    String line = null;
                    if ((line = reader.readLine()) != null) {
                        System.out.println(line + " line");
                    }
                    if (line == null || line.contains(" ")) {
                        System.out.println("Incorrect data");
                        n = 0;
                        return;
                    }
                    n = Integer.valueOf(line);
                    if (n < 1 || n > 5) {
                        System.out.println("Incorrect data");
                        n = 0;
                        return;
                    }
                    System.out.println(n + " n");
                    for (int i = 0; i < n; i++) {
                        String test = reader.readLine();
                        List<String> data = new ArrayList<>(Arrays.asList(test.split(" ")));
                        data.removeIf(s -> s.equals(""));
                        if (data.size() > 12) {
                            System.out.println("Incorrect data");
                            n = 0;
                            return;
                        }
                        List<BoxPanel> panels = new ArrayList<>();
                        for (int j = 0; j < 12; j += 2) {
                            if (Integer.valueOf(data.get(j)) > 10000 || Integer.valueOf(data.get(j + 1)) > 10000) {
                                n = 0;
                                return;
                            }
                            panels.add(new BoxPanel(Integer.valueOf(data.get(j)), Integer.valueOf(data.get(j+1))));
                        }
                        allData.add(new TestData(panels));
                        System.out.println(i + " i in reading");
                    }
                } catch (IOException x) {
                    System.err.println(x);
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
