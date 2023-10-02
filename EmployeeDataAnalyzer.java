import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class EmployeeRecord {
    String name;
    String position;
    Date timeIn;
    Date timeOut;

    public EmployeeRecord(String name, String position, Date timeIn, Date timeOut) {
        this.name = name;
        this.position = position;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }
}

public class EmployeeDataAnalyzer {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    public static void main(String[] args) {
        // Read data from the file and populate employee records
        List<EmployeeRecord> records = loadDataFromFile("D:/Assignment_Timecard.csv");

        // Analyze employee records
        analyzeEmployeeRecords(records);
    }

    private static void analyzeEmployeeRecords(List<EmployeeRecord> records) {
        for (int i = 0; i <= records.size() - 7; i++) {
            if (hasConsecutiveDays(records, i, 7)) {
                System.out.println("Employee Name: " + records.get(i).name + ", Position: " + records.get(i).position +
                        " has worked for 7 consecutive days.");
                i += 6; // Skip records for this set of consecutive days
            }
        }

        for (int i = 0; i < records.size() - 1; i++) {
            Date currentShiftEnd = records.get(i).timeOut;
            Date nextShiftStart = records.get(i + 1).timeIn;
            long hoursBetween = (nextShiftStart.getTime() - currentShiftEnd.getTime()) / (60 * 60 * 1000);
            if (hoursBetween < 10 && hoursBetween > 1) {
                System.out.println("Employee Name: " + records.get(i).name + ", Position: " + records.get(i).position +
                        " has less than 10 hours but greater than 1 hour between shifts.");
            }
        }

        for (EmployeeRecord record : records) {
            long shiftDuration = (record.timeOut.getTime() - record.timeIn.getTime()) / (60 * 60 * 1000);
            if (shiftDuration > 14) {
                System.out.println("Employee Name: " + record.name + ", Position: " + record.position +
                        " has worked for more than 14 hours in a single shift.");
            }
        }
    }

    private static boolean hasConsecutiveDays(List<EmployeeRecord> records, int startIndex, int consecutiveDays) {
        for (int i = startIndex; i < startIndex + consecutiveDays - 1; i++) {
            if (!isConsecutiveDays(records.get(i).timeOut, records.get(i + 1).timeIn)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isConsecutiveDays(Date timeOut, Date timeIn) {
        long diffInMilliseconds = Math.abs(timeIn.getTime() - timeOut.getTime());
        long diffInDays = diffInMilliseconds / (24 * 60 * 60 * 1000);
        return diffInDays == 1;
    }

    private static List<EmployeeRecord> loadDataFromFile(String filePath) {
        List<EmployeeRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;  // Flag to skip the first line (header row)
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;  // Skip the header row
                }
                String[] data = line.split(",");
                String name = data[0].trim();
                String position = data[1].trim();
                String timeInStr = data[2].trim();
                String timeOutStr = data[3].trim();
    
                // Check for empty or blank date strings
                if (!timeInStr.isEmpty() && !timeOutStr.isEmpty()) {
                    try {
                        Date timeIn = DATE_FORMAT.parse(timeInStr);
                        Date timeOut = DATE_FORMAT.parse(timeOutStr);
                        records.add(new EmployeeRecord(name, position, timeIn, timeOut));
                    } catch (java.text.ParseException e) {
                        System.out.println("Error parsing date in line: " + line);
                    }
                } else {
                    System.out.println("Empty or blank date found in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }
}
