import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClientTest extends JFrame implements ActionListener{
    public static boolean inputCheck = false;
    JButton btnStart; //보내기버튼
    JButton btnStop; //종료버튼
    JTextField tfName; //메세지 입력창
    JTextArea taMessage; //메세지 출력화면
    private static Socket socket;
    private static OutputStream outputStream;
    private static DataOutputStream dos;

    public ClientTest() { //UI 제작
        setTitle("사용기록 체크기");
        taMessage = new JTextArea();
        taMessage.setFont(new Font("맑은 고딕",Font.BOLD,15));
        taMessage.setEditable(false);
        JScrollPane scroll = new JScrollPane(taMessage);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  //항상 스크롤바가 세로로 떠있음

        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        tfName = new JTextField();

        btnStart = new JButton("보내기");
        btnStop = new JButton("종료");
        btnStop.addActionListener(this);
        btnStart.addActionListener(this);

        bottom.add("Center",tfName);
        bottom.add("East",btnStart);

        Container c = this.getContentPane();
        c.add("Center", scroll);
        c.add("South", bottom);
        bottom.add("South",btnStop);
        //윈도우 창 설정
        setBounds(300,300,300,300);
        setVisible(true);

    }

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 7777);
            outputStream = socket.getOutputStream();
            dos = new DataOutputStream(outputStream);
            System.out.println("연결되었습니다.");
        } catch (UnknownHostException e) {
            System.out.println("서버를 찾을 수 없습니다.");
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            System.out.println("서버와 연결이 안되었습니다.");
            e.printStackTrace();
            System.exit(0);
        } //서버를 킬 경우 주석 삭제
        new ClientTest();
    }
    public void actionPerformed(ActionEvent e) {
        String name = tfName.getText(); // name 전달방식 수정 필요
        String line = "["+getTime()+"] "+name;
        taMessage.append(name+"님, 안녕하세요\n"+"입장시간 : " + "["+getTime()+"]\n");
        try {
            dos.writeUTF(line);
        } catch (IOException ee) {
            ee.getStackTrace();
        }
        Timer tm = new Timer();
        String m = "";

        if(e.getSource() == btnStop) {
            try {
                socket.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
            System.exit(0);
        }
        else if(e.getSource() == btnStart) {
            while(true) {
                if(tm.isAlive()) {
                    tm.interrupt();
                }
                taMessage.append("\n"); //name 수정 필요
                inputCheck = false;
                tm = new Timer();
                setVisible(false); //안보이게 수정

                try{
                    TimeUnit.SECONDS.sleep(3); //공부시간
                } catch (InterruptedException ee) {
                    ee.getStackTrace();
                }
                setVisible(true); //다시 보이게

                tm.start(); //타이머 시작
                /*
                do {
                    m =JOptionPane.showInputDialog(null,"계속 수업을 진행하려면 \"계속\"을 입력해주세요\n" + "수업을 그만하려면 \"퇴장\"을 입력해주세요", "시간 만료", JOptionPane.INFORMATION_MESSAGE);
                } while(!m.equals("계속") && !m.equals("퇴장"));

                if(m.equals("계속")) {
                    inputCheck = true;
                }
                else {
                }

                 */
                int result = JOptionPane.showConfirmDialog(null, "계속?", "제목", JOptionPane.YES_NO_OPTION);

                if(result == JOptionPane.YES_OPTION) {
                    inputCheck = true;
                }
                else {
                }

            }
        }
    }
    class Timer extends Thread{
        @Override
        public void run() {
            for(int i=10; i>=1; i--){
                if(ClientTest.inputCheck==true){
                    return;
                }
                System.out.println(i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            try {
                dos.writeUTF("bye "+"[" +  getTime() + "]");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }


    static String getTime() { //시간 가공을 위해
        SimpleDateFormat f = new SimpleDateFormat("hh:mm:ss");
        return f.format(new Date());
    }
}
