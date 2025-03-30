package app.infonitesave.data;

import java.time.LocalDate;

public class URL {

    public static String quality(LocalDate date) {

        return "https://logistics.amazon.com/performance/api/v1/getData?dataSetId=da_dsp_station_daily_supplemental_quality&dsp=LVEL&from=" +
                date +
                "&station=DCL5&timeFrame=Daily&to=" +
                date;

    }

    public static String drivers() {

        return "https://logistics.amazon.com/performance/api/v1/fetchDSPAssociates?companyId=59b162d3-7dd4-4141-920d-13fd2293e565&operationalStatuses=ACTIVE%2CINACTIVE";

    }

}
