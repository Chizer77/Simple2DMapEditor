/*
 * Copyright (c) 2023.
 * @author Chizer
 */



public interface MapConfig {
    //每个素材大小
    int elemWidth = 32;
    int elemHeight = 32;

    //地图大小 960 * 640
    int MapWidth = 1920;    //一面30个,共两面,地图尺寸最好只增大不减小，否则需要改动地图初始化
    int MapHeight = 640;    //20个

    //图片路径前缀
    public static String imagePath = System.getProperty("user.dir") + "/src/images/mapImage.png";

    //保存地图默认路径
    public static String mapPath = System.getProperty("user.dir") + "/src/map/";
}
