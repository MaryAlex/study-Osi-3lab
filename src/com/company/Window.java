package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Window extends JFrame {
    public static final String FILE_END_IN = "IN";
    private List<TestData> allData = new ArrayList<>();
    private List<Boolean> answers = new ArrayList<>();
    private Thread cheakerIn = new Thread(checkerIn());
    private Thread solver = new Thread(solver());
    private Thread cheakerOut;
    private JPanel panel;
    private JButton startButton;
    private JButton stopButton;

    public Window() {
        super("Hello");
        setContentPane(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        startButton.addActionListener(startWork());

        setVisible(true);
    }

    private ActionListener startWork() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cheakerIn.run();
                solver.run();
            }
        };
    }

    private Runnable solver() {
        return new Runnable() {
            @Override
            public void run() {
                while (allData.size() == 0);
                TestData tmp = allData.get(0);
                allData.remove(0);
                answers.add(tmp.isBox());
            }
        };
    }

    private Runnable checkerIn() {
        return new Runnable() {
            @Override
            public void run() {
                String file = "/home/mary/Work/Java/Osi/3lab/src/box.txt";
                try (FileInputStream in = new FileInputStream(file);
                     BufferedReader reader =
                             new BufferedReader(new InputStreamReader(in))) {
                    String line = null;
                    if ((line = reader.readLine()) != null) {
                        System.out.println(line + " line");
                    }
                    if (line == null || line.contains(" ")) {
                        System.out.println("Incorrect data");
                        return;
                    }
                    Integer n = Integer.valueOf(line);
                    if (n < 1 || n > 5) {
                        System.out.println("Incorrect data");
                        return;
                    }
                    System.out.println(n + " n");
                    for (int i = 0; i < n; i++) {
                        String test = reader.readLine();
                        List<String> data = new ArrayList<>(Arrays.asList(test.split(" ")));
                        data.removeIf(s -> s.equals(""));
                        if (data.size() > 12) {
                            System.out.println("Incorrect data");
                            return;
                        }
                        List<BoxPanel> panels = new ArrayList<>();
                        for (int j = 0; j < 6; j++) {
                            if (Integer.valueOf(data.get(j)) > 10000 || Integer.valueOf(data.get(j + 6)) > 10000) {
                                return;
                            }
                            panels.add(new BoxPanel(Integer.valueOf(data.get(j)), Integer.valueOf(data.get(j+6))));
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
//    public String openL() {
//        String path = null;
//        JFileChooser c = new JFileChooser();
//        FileNameExtensionFilter filter = new FileNameExtensionFilter(FILE_END_IN, FILE_END_IN);
//        c.setFileFilter(filter);
//        // c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        int rVal = c.showOpenDialog(Window.this);
//        if (rVal == JFileChooser.APPROVE_OPTION) {
//            path = c.getSelectedFile().toString();
//        }
//        if (rVal == JFileChooser.CANCEL_OPTION) {
//            /* no-op */
//        }
//        return path;
//    }
}
