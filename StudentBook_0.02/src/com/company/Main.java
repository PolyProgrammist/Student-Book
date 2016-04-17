package com.company;

public class Main {
    public static String[] lessonFileName;
    public static ChooseLessonMenu[] topics;
    public static ChooseLessonMenu[] classes;
    public static int[][] connections;
    public static int[][] anticonnections;
    public static String[] profiles;

    public static void loadEnvironmentFromFiles(){
        String spc = PathConstants.FL;
        Reader rd = new Reader(spc + PathConstants.TREES_WAY, PathConstants.ID_TO_LES_FILE_NAME);
        lessonFileName = rd.nextStringArray();
        rd.setFileName(PathConstants.CONNECTIONS_FILE_NAME);
        connections = rd.nextIntArrayArray();
        anticonnections = GoodFunctions.makeTransParentGraph(connections);
        rd.setFileName(PathConstants.CLASSES_FILE_NAME);
        classes = rd.nextCLM_Array();
        rd.setFileName(PathConstants.TOPICS_FILE_NAME);
        topics = rd.nextCLM_Array();
        rd.setPath(spc + PathConstants.PROF_INFO_WAY, PathConstants.PROFILES_FILE_NAME);
        profiles = rd.nextStringArray();
        rd.closeReader();
    }

    public static void main(String[] args) {
        loadEnvironmentFromFiles();
	    MainGUI.launch();
    }
}
