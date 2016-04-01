package com.company;

public class Main {
    public static String[] lessonFileName;
    public static ChooseLessonMenu[] topics;
    public static ChooseLessonMenu[] classes;
    public static int[][] connections;
    public static int[][] anticonnections;
    public static String[] profiles;
    public static void main(String[] args) {
        Reader rd = new Reader(Reader.DEFAULT_TREES_WAY, Reader.DEFAULT_ID_TO_LES_FILE_NAME);
        lessonFileName = rd.nextStringArray();
        rd.setFileName(Reader.DEFAULT_CONNECTIONS_FILE_NAME);
        connections = rd.nextIntArrayArray();
        anticonnections = GoodFunctions.makeTransParentGraph(connections);
        rd.setFileName(Reader.DEFAULT_CLASSES_FILE_NAME);
        classes = rd.nextCLM_Array();
        rd.setFileName(Reader.DEFAULT_TOPICS_FILE_NAME);
        topics = rd.nextCLM_Array();
        rd.setPath(Reader.DEFAULT_PROF_INFO_WAY, Reader.DEFAULT_PROFILES_FILE_NAME);
        profiles = rd.nextStringArray();
	    MainMenuBar.launch();
    }
}
