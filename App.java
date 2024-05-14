import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class App {

    static JFrame frame;
    static JPanel panel;
    static JButton[] buttons;
    static ArrayList<Integer> userPlayer;
    static ArrayList<Integer> computerPlayer;
    static String[] playersMark = {"X", "O"};
    static int[] rows = {0, 3, 6};
    static int[] columns = {0, 1, 2};
    public static void main(String[] args) throws Exception {
        frame = new JFrame();
        panel = new JPanel();
        buttons = new JButton[9];
        userPlayer = new ArrayList<Integer>();
        computerPlayer = new ArrayList<Integer>();

        panel.setLayout(new GridLayout(3, 3));
        frame.add(panel);

        createBoard();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static void createBoard() {
        for (int i=0; i<9; i++) {
            int j =i;
            buttons[i] = new JButton();
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setBorderPainted(true);
            buttons[i].setFocusPainted(false);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 100));
            buttons[i].addActionListener(e -> userChoice(j));
            panel.add(buttons[i]);
            panel.revalidate();
            panel.repaint();
        }
    }

    static void userChoice(int indx) {
        buttons[indx].setText(playersMark[0]);
        buttons[indx].setEnabled(false);
        userPlayer.add(indx);
        enableButtons(false);
        checkResult("user", indx);
    }

    static void computerChoice() {
        int choice = computerPro();
        computerPlayer.add(choice);
        buttons[choice].setEnabled(false);
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttons[choice].setText(playersMark[1]);
                checkResult("computer", choice);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    static void checkResult(String player, int index) {
        switch (player) {
            case "user":
                int[] userPattren = pattrenCheck(userPlayer);
                if (userPattren[0] < 9) {
                    for (int p: userPattren) {
                        buttons[p].setBackground(Color.cyan);
                    }
                    gameOver("You win!");
                    return;
                }
                if (userPlayer.size() + computerPlayer.size() == 9) {
                    gameOver("Draw");
                    return;
                }
                computerChoice();
                break;
        
            default:
                int[] computerPattren = pattrenCheck(computerPlayer);
                if (computerPattren[0] < 9) {
                    for (int p: computerPattren) {
                        buttons[p].setBackground(Color.cyan);
                    }
                    gameOver("Computer win");
                    return;
                }
                if (userPlayer.size() + computerPlayer.size() == 9) {
                    gameOver("Draw");
                }
                enableButtons(true);
                break;
        }
    }

    static int[] pattrenCheck(ArrayList<Integer> playerArray) {
        int[] pattren = {9, 9, 9};

        for (int num: playerArray) {
            if (isHolds(rows, num) &&
            playerArray.contains(num+1) && playerArray.contains(num+2)) {
                pattren[0] = num;
                pattren[1] = num+1;
                pattren[2] = num+2;
            }
            else if (isHolds(columns, num) &&
            playerArray.contains(num+3) && playerArray.contains(num+6)) {
                pattren[0] = num;
                pattren[1] = num+3;
                pattren[2] = num+6;
            }
            else if (num == 4) {
                if (playerArray.contains(2) && playerArray.contains(6)) {
                    pattren[0] = 2;
                    pattren[1] = 4;
                    pattren[2] = 6;
                }
                else if (playerArray.contains(0) && playerArray.contains(8)) {
                    pattren[0] = 0;
                    pattren[1] = 4;
                    pattren[2] = 8;
                }
            }
        }

        return pattren;
    }

    static int computerPro() {
        List<Integer> cpuChoice = IntStream.range(0, 9).boxed().collect(Collectors.toList());
        Collections.shuffle(cpuChoice);
        cpuChoice.removeIf(n -> (userPlayer.contains(n)));
        cpuChoice.removeIf(n -> (computerPlayer.contains(n)));

        ArrayList<Integer> computerCopy = new ArrayList<>(computerPlayer);
        for (int choice: cpuChoice) {
            computerCopy.add(choice);
            if (pattrenCheck(computerCopy)[0] < 9) {
                return choice;
            }
            computerCopy.remove(computerCopy.indexOf(choice));
        }

        ArrayList<Integer> userCopy = new ArrayList<>(userPlayer);
        for (int choice: cpuChoice) {
            userCopy.add(choice);
            if (pattrenCheck(userCopy)[0] < 9) {
                return choice;
            }
            userCopy.remove(userCopy.indexOf(choice));
        }

        return cpuChoice.get(0);
    }

    static void gameOver(String msg) {
        Timer timer1 = new Timer(500, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Restart", "Exit"};
                int dialog = JOptionPane.showOptionDialog(
                    frame, "Play again?", msg, JOptionPane.NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (dialog == 0) {
                    restartGame();
                }
                else {
                    frame.dispose();
                }
            }
            
        });
        timer1.setRepeats(false);
        timer1.start();
    }

    static void restartGame() {
        userPlayer.clear();
        computerPlayer.clear();
        
        for (JButton button: buttons) {
            button.setText(null);
            button.setBackground(Color.WHITE);
            button.setEnabled(true);
        }
        panel.repaint();
        panel.revalidate();
    }

    static void enableButtons(boolean bool) {
        for (int i=0; i<buttons.length; i++) {
            if (userPlayer.contains(i) || computerPlayer.contains(i)) {
                continue;
            }
            buttons[i].setEnabled(bool);
        }
    }

    static boolean isHolds(int[] intArray, int numb) {
        for (int a: intArray) {
            if (a == numb) {
                return true;
            }
        }
        return false;
    }
}
