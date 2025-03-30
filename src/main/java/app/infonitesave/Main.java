package app.infonitesave;

import app.infonitesave.data.Data;
import app.infonitesave.data.drivers.Drivers;
import app.infonitesave.data.SuppReports;
import app.infonitesave.messages.Messages;
import app.infonitesave.output.Output;
import app.infonitesave.utils.helpers.WhatsAppAPI;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        //Save Data
        try {
            //Drivers.updateDrivers();
            SuppReports.saveSuppReports(LocalDate.now().minusDays(7));
        } catch (Exception ignored) {}
        try {
            //Data.saveDay(LocalDate.now().minusDays(1));
        } catch (Exception ignored) {}

        //Send Messages
        try {
            //Messages.sendDaily(LocalDate.now().minusDays(1));

        } catch (Exception ignored) {}

        //Generate Files
        try {
            Output.createSheets();
        } catch (Exception ignored) {}


    }
}