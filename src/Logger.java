enum LogLevel {
    Info,
    Warn,
    Error
}

class Logger {
    private static String GREEN_FG = "\u001B[32m";
    private static String YELLOW_FG = "\u001B[33m";
    private static String RED_FG = "\u001B[31m";
    private static String RESET = "\u001B[0m";

    private static int LOG_ERR = 0;
    private static int LOG_WARN = 1;
    private static int LOG_INFO = 2;

    // 000 -> None
    // 001 -> Error
    // 011 -> Warning, Error
    // 111 -> Info, Warning, Error (Default)
    private static byte currentLevel = 0b111;

    public static void SetLevel(LogLevel l) {
        switch (l) {
            case Info:
                currentLevel = 0b111;
                break;
            case Warn:
                currentLevel = 0b011;
                break;
            case Error:
                currentLevel = 0b001;
                break;
            default:
                Unreachable();
        }
    }

    public static void Unreachable() {
        Panic("UNREACHABLE STATEMENT");
    }

    public static void Panic(String message) {
        Error(message);
        throw new LoggerException();
    }

    public static void Fatal(String message) {
        Error(message);
        System.exit(1);
    }

    public static void Divider() {
        System.out.println("-----------------------");
    }

    public static void Info(String message) {
        if ((currentLevel & (1 << LOG_INFO)) != 0)
            System.out.println("[INFO] " + message);
    }

    public static void Warn(String message) {
        if ((currentLevel & (1 << LOG_WARN)) != 0)
            System.out.println(YELLOW_FG + "[WARN] " + message + RESET);
    }

    public static void Error(String message) {
        if ((currentLevel & (1 << LOG_ERR)) != 0)
            System.out.println(RED_FG + "[ERROR] " + message + RESET);
    }
}

class LoggerException extends RuntimeException {
    public LoggerException() {
        super();
    }
}
