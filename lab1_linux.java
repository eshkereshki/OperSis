import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SysInfoLinux {
    public static void main(String[] args) throws Exception {
        System.out.println("=== System Info (Linux) ===\n");

        System.out.println("OS: " + exec("lsb_release -d | cut -f2"));
        System.out.println("Ядро: " + exec("uname -sr"));
        System.out.println("Архитектура: " + exec("uname -m"));
        System.out.println("Имя хоста: " + exec("hostname"));
        System.out.println("Пользователь: " + exec("whoami"));

        System.out.println("Оперативная память: " + exec("free -m | grep '^Mem:' | tr -s ' ' | cut -d' ' -f4,2 | sed 's/ / МБ свободно /; s/$/ МБ всего/'"));
        System.out.println("Процессоры: " + exec("nproc"));

        String swapLine = exec("free -m | grep '^Swap:'");
        String[] swap = swapLine.trim().split("\\s+");
        System.out.printf("Раздел подкачки: %s МБ всего / %s МБ свободно%n", swap[1], swap[3]);

        String vmLine = exec("grep '^VmallocTotal:' /proc/meminfo");
        String vmKB = vmLine.isEmpty() ? "0" : vmLine.split("\\s+")[1];
        long vmMB = Long.parseLong(vmKB) / 1024;
        System.out.printf("Виртуальная память: %,d МБ%n", vmMB);

        System.out.println("\n--- Диски ---");
        System.out.println(exec("df -h --output=source,fstype,size,used"));

    }

    // Метод для запуска команд Linux и возврата их вывода
    private static String exec(String cmd) throws Exception {
        Process process = new ProcessBuilder("bash", "-c", cmd).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        process.waitFor();
        return output.toString().trim();
    }
}
