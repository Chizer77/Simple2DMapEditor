/*
 * Copyright (c) 2023.
 * @author Chizer
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图形化设置地图数组
 * @author Chizer
 */
public class SetMap extends JFrame implements MapConfig {
    //用来选择素材的下拉表
    JComboBox<ImageIcon> box;
    //选择素材图层层次的下拉表
    JComboBox<Integer> boxType;

    //存储建立的地图数组（1为背景素材，2为地面素材，3为上层素材）
    static int[][] map1 = new int[MapHeight/elemHeight][MapWidth/elemWidth];     //根据地图大小/素材长宽决定
    static int[][] map2 = new int[MapHeight/elemHeight][MapWidth/elemWidth];
    static int[][] map3 = new int[MapHeight/elemHeight][MapWidth/elemWidth];

    static ImageIcon[][] icon1 = new ImageIcon[MapHeight/elemHeight][MapWidth/elemWidth];
    static ImageIcon[][] icon2 = new ImageIcon[MapHeight/elemHeight][MapWidth/elemWidth];
    static ImageIcon[][] icon3 = new ImageIcon[MapHeight/elemHeight][MapWidth/elemWidth];
    //编辑地图面板
    static JPanel panel;

    /**
     * 设置窗体
     */
    public void init() {
        this.setTitle("2D地图编辑器");
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);

        //素材下拉表
        box = new JComboBox<>();
        setBox(box);
        box.setPreferredSize(new Dimension(70,50));
        box.setBackground(Color.white);

        //设置字体
        Font myFont = new Font("微软雅黑", Font.BOLD, 15);

        //图层选择
        boxType = new JComboBox<>();
        boxType.addItem(1);
        boxType.addItem(2);
        boxType.addItem(3);
        boxType.setPreferredSize(new Dimension(70, 50));
        boxType.setFont(myFont);
        boxType.setBackground(Color.white);

        //地图面板
        panel = new MySetPanel();
        panel.setPreferredSize(new Dimension(MapWidth, MapHeight)); //地图大小

        //地图面板加入jsp
        JScrollPane jsp = new JScrollPane(panel);
        jsp.setPreferredSize(new Dimension(700, MapHeight));  //地图显示范围
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  //水平滑动条始终显示
        JScrollBar bar = jsp.getHorizontalScrollBar();
        jsp.getViewport().setBackground(Color.white);
        bar.setBackground(Color.white);


        //保存按钮
        JButton create = new JButton("保存地图");
        create.setActionCommand("create");  //按钮触发关键词
        create.setFont(myFont);
        create.setPreferredSize(new Dimension(100, 50));
        create.setBackground(Color.white);

        this.add(jsp, BorderLayout.CENTER);

        FlowLayout ft = new FlowLayout();
        ft.setHgap(20);
        JPanel p1 = new JPanel(ft);
        p1.add(box);
        p1.add(boxType);
        p1.add(create);
        p1.setPreferredSize(new Dimension(1000, 800 - MapHeight - 53));
        this.add(p1, BorderLayout.SOUTH);

        this.setVisible(true);
        this.setResizable(false);

        PanelListener pils = new PanelListener();
        panel.addMouseListener(pils);
        ButtonListener bils = new ButtonListener();
        create.addActionListener(bils);

        //刷新面板线程
//        UpdateThread updateThread = new UpdateThread(panel);
//        updateThread.start();

    }


    /**
     * 地图面板类
     * @author Chizer
     */
    class MySetPanel extends JPanel {

        //初始地图面板读入先前的地图
        public MySetPanel() {
            //初始背景map1全部填充为1.png
            for(int i = 0; i < map1.length; i++) {
                for (int j = 0; j < map1[0].length; j++) {
                    map1[i][j] = 1;
                    icon1[i][j] = box.getItemAt(1);
                }
            }
            //初始读入先前保存的地图
            try {
                //输入流
                File file = new File(mapPath + "myMap.map");
                if(file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    DataInputStream dis = new DataInputStream(fis);
                    int i = dis.readInt();
                    int j = dis.readInt();
                    for(int ii = 0; ii < i; ii++) {
                        for(int jj = 0; jj < j; jj++) {
                            map1[ii][jj] = dis.readInt();
                            map2[ii][jj] = dis.readInt();
                            map3[ii][jj] = dis.readInt();
                            icon1[ii][jj] = box.getItemAt(map1[ii][jj]);
                            icon2[ii][jj] = box.getItemAt(map2[ii][jj]);
                            icon3[ii][jj] = box.getItemAt(map3[ii][jj]);
                        }
                    }
                    dis.close();
                    fis.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for(int i = 0; i < MapHeight/elemHeight; i++) {
                for(int j = 0; j < MapWidth/elemWidth; j++) {
                    //第一层元素
                    if(icon1[i][j] != null) g.drawImage(icon1[i][j].getImage(), j*32, i*32, elemWidth, elemHeight, null);
                    //第二层
                    if(icon2[i][j] != null) g.drawImage(icon2[i][j].getImage(), j*32, i*32, elemWidth, elemHeight, null);
                    //第三层
                    if(icon3[i][j] != null) g.drawImage(icon3[i][j].getImage(), j*32, i*32, elemWidth, elemHeight, null);
                }
            }
        }
    }

    /**
     * 面板监听类
     * @author Chizer
     */
    class PanelListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {    //地图添加元素后刷新面板
            //获得点击位置下标
            int j = e.getX()/elemWidth;
            int i = e.getY()/elemHeight;
            System.out.println("Click <" + i + ", " + j + ">");

            //获取当前选择的元素
            ImageIcon selectedIcon = (ImageIcon) box.getSelectedItem();
            //获取选择元素的编号,截取编号
            assert selectedIcon != null;
            String filename = selectedIcon.toString();
            int num = Integer.parseInt(filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")));
            System.out.println("Select icon: " + num);

            //图层位置
            int layer = (int)boxType.getSelectedItem();
            switch (layer) {
                case 1:
                    map1[i][j] = num;
                    icon1[i][j] = selectedIcon;
                    break;
                case 2:
                    map2[i][j] = num;
                    icon2[i][j] = selectedIcon;
                    break;
                case 3:
                    map3[i][j] = num;
                    icon3[i][j] = selectedIcon;
                    break;
                default:
            }
            panel.repaint();
        }
    }

//    //刷新地图面板线程
//    public class UpdateThread extends Thread {
//        JPanel panel;
//        public UpdateThread(JPanel panel) {
//            this.panel = panel;
//        }
//        @Override
//        public void run() {
//            while(true){
//                panel.repaint();
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    /**
     * 按钮监听类
     * @author Chizer
     */
    static class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {    //按下生成地图后，保存地图至指定路径
            if(e.getActionCommand().equals("create")) {
                System.out.println("开始保存");
                try {
                    //文件输出流
                    File file = new File(mapPath);
                    file.mkdirs();
                    FileOutputStream fos = new FileOutputStream(mapPath + "myMap.map");
                    System.out.println("保存至：" + mapPath + "myMap.map");
                    //将输出流转化为基本数据输出流
                    DataOutputStream dos = new DataOutputStream(fos);
                    //各个地图数组大小
                    int height = map1.length;
                    int width = map1[0].length;

                    //先写入大小
                    dos.writeInt(height);
                    dos.writeInt(width);
                    //将三个地图数组依次写入
                    for(int i = 0; i < height; i++) {
                        for(int j = 0; j < width; j++) {
                            dos.writeInt(map1[i][j]);
                            dos.writeInt(map2[i][j]);
                            dos.writeInt(map3[i][j]);
                        }
                    }
                    //将缓存区数据全部输出清空
                    dos.flush();
                    //关闭输出流
                    dos.close();
                    System.out.println("保存完成");
                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }

            }
        }
    }

    /**
     * box裁剪后加入图片
     * @param box
     */
    public void setBox(JComboBox box) {
        //图片数量
        int count = 0;
        int[] elemSize = {elemWidth, elemHeight};  //元素大小
        //裁剪图片
        try {
            BufferedImage item = ImageIO.read(new File(imagePath));
            int heightCount = item.getHeight() / elemSize[1];
            int widthCount = item.getWidth() / elemSize[0];
            for(int i = 0; i < heightCount; i++) {
                for(int j = 0; j < widthCount; j++) {
                    File file = new File("src/images/" + ++count + ".png");
                    if(!file.exists()) {
                        BufferedImage it = item.getSubimage(j * elemSize[0], i * elemSize[1], elemSize[0], elemSize[1]);
                        ImageIO.write(it, "png", file);
                        System.out.println("image " + count + " init");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //加入box
        box.addItem(new ImageIcon("src/images/0.png"));
        for(int i = 0; i < count; i++) {
            box.addItem(new ImageIcon("src/images/" + (i + 1) + ".png"));
        }
    }







    /**
     *
     * @param args 启动窗口
     */
    public static void main(String[] args) {
        SetMap mp = new SetMap();
        mp.init();
    }
}
