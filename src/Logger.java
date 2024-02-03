class Logger {
    private static String GREEN_FG = "\u001B[32m";
    private static String YELLOW_FG = "\u001B[33m";
    private static String RED_FG = "\u001B[31m";
    private static String RESET = "\u001B[0m";

    public static void panic(String message) {
        error(message);
        throw new LoggerException();
    }

    public static void fatal(String message) {
        error(message);
        System.exit(1);
    }

    public static void info(String message) {
        System.out.println("INFO: " + message + RESET);
    }

    public static void warn(String message) {
        System.out.println(YELLOW_FG + "WARN: " + message + RESET);
    }

    public static void error(String message) {
        System.out.println(RED_FG + "ERROR: " + message + RESET);
    }
}

class LoggerException extends RuntimeException {
    public LoggerException() {
        super();
    }
}
