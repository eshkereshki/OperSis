import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.sun.management.OperatingSystemMXBean;

public class SysInfoWinAPI {
    public static void main(String[] args) {
        try {
            System.out.println("ОС: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));

            String host = InetAddress.getLocalHost().getHostName();
            System.out.println("Имя компьютера: " + host);
            System.out.println("Пользователь: " + System.getProperty("user.name"));

            System.out.println("Архитектура: " + System.getProperty("os.arch"));

            OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            System.out.println("Процессоры: " + os.getAvailableProcessors());

            // Физическая память
            long totalPhysicalMB = os.getTotalPhysicalMemorySize() / (1024 * 1024);
            long freePhysicalMB = os.getFreePhysicalMemorySize() / (1024 * 1024);
            long usedPhysicalMB = totalPhysicalMB - freePhysicalMB;
            double memoryLoad = totalPhysicalMB > 0 ? (usedPhysicalMB * 100.0 / totalPhysicalMB) : 0.0;

            System.out.println("Оперативная память: " + usedPhysicalMB + " МБ / " + totalPhysicalMB + " МБ");
            System.out.printf("Загрузка памяти: %.0f%%%n", memoryLoad);

            // Файл подкачки (swap)
            long totalSwapMB = os.getTotalSwapSpaceSize() / (1024 * 1024);
            long freeSwapMB = os.getFreeSwapSpaceSize() / (1024 * 1024);
            long usedSwapMB = totalSwapMB - freeSwapMB;

            System.out.printf("Файл подкачки: %d МБ / %d МБ%n", usedSwapMB, totalSwapMB);

            // Виртуальная память = физическая + подкачка
            long totalVirtualMB = totalPhysicalMB + totalSwapMB;
            long usedVirtualMB = usedPhysicalMB + usedSwapMB;
            System.out.println("Виртуальная память: " + usedVirtualMB + " МБ / " + totalVirtualMB + " МБ");

            System.out.println("\nДиски:");
            for (File root : File.listRoots()) {
                long totalGB = root.getTotalSpace() / (1024L * 1024L * 1024L);
                long freeGB = root.getFreeSpace() / (1024L * 1024L * 1024L);
                System.out.printf(" - %s  %d ГБ свободно / %d ГБ всего%n", root.getPath(), freeGB, totalGB);
            }

        } catch (UnknownHostException e) {
            System.err.println("Ошибка получения имени хоста: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
