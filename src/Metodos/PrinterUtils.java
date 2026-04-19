package Metodos;

import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;
import javax.print.PrintService;

public final class PrinterUtils {

    private PrinterUtils() {
    }

    public static List<String> getInstalledPrinterNames() {
        List<String> printers = new ArrayList<String>();
        PrintService[] services = PrinterJob.lookupPrintServices();
        for (PrintService service : services) {
            printers.add(service.getName());
        }
        return printers;
    }
}
