package at.diwh.comfortableduration.main;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;

import at.diwh.comfortableduration.util.ComfortableDuration;

import java.util.TimeZone;

/**
 * @author 246J
 */
public class Main {

    /**
     * @param args - Argumente
     */
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        System.out.println("Dies ist eine Demonstration für die ComfortableDuration-Klasse.");
        System.out.println(
                "Als Beispiel wird ausgerechnet, wie viel Zeit man für CATS eintragen müsste, wenn man um 6:00 den Arbeitstag beginnt.");
        LocalDate hierUndHeute = LocalDate.now();
        LocalDateTime genauJetzt = LocalDateTime.now();

        // Start des Tages
        LocalDateTime tagesstart = LocalDateTime.of(hierUndHeute.getYear(), hierUndHeute.getMonth(),
            hierUndHeute.getDayOfMonth(), 6, 0);

        Duration arbeitszeit = Duration.between(tagesstart, genauJetzt);

        System.out.println("Anwesenheitszeit: " + arbeitszeit);
        arbeitszeit = arbeitszeit.minus(30, ChronoUnit.MINUTES); // 30 Minuten Mittagspause weg
        System.out.println("MP weg -> Arbeitszeit: " + arbeitszeit);

        ComfortableDuration ergebnis = new ComfortableDuration(arbeitszeit);

        System.out.println(ergebnis.toString());

        int minutenIn25 = (ergebnis.getMinuten().intValue() / 15) * 25;
        System.out.println("\n\nDas sind " + ergebnis.getStunden() + "," + minutenIn25 + " für CATS\n");
        System.out.println(" --------------------------------------------------------------------------------");

        System.out.println(" *** Und nun ein paar Tests für die Datumsfunktionen");
        Date datum = new Date();
        System.out.println("Heutiges Datum RAW: " + datum);

        final String fullTimestampFormatString = "yyyyMMddHHmmssSSS"; // Millisekunden, max. Auflösung von Date
        final String fullReadableTSFormatString = "yyyy-MM-dd HH:mm:ss.SSS"; // Millisekunden, max. Auflösung von Date
        final String aktuellerOrt = "Europe/Vienna";
        final Locale aktuelleSprache = Locale.GERMAN;
        final TimeZone timezone = TimeZone.getTimeZone(aktuellerOrt);
        final TimeZone timezoneCC = TimeZone.getTimeZone("Canada/Central");

        final SimpleDateFormat df = new SimpleDateFormat(fullTimestampFormatString, aktuelleSprache);
        final SimpleDateFormat readableDf = new SimpleDateFormat(fullReadableTSFormatString, aktuelleSprache);
        df.setTimeZone(timezone);
        readableDf.setTimeZone(timezone);

        // String[] alleTimeZones = TimeZone.getAvailableIDs();
        // for (String element : alleTimeZones) {
        // System.out.println("TZ: " + element);
        // }

        Instant instJetzt = Instant.now(); // Start Messung mit Instant

        String timestamp = df.format(datum);
        String readableTS = readableDf.format(datum);

        System.out.println("Timestamp mit Millisekunden: " + timestamp);
        System.out.println("Lesbarer TS mit Millisekunden: " + readableTS);

        String now = "2016-11-09 10:30";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime formatDateTime = LocalDateTime.parse(now, formatter);

        System.out.println("Before : " + now);

        System.out.println("After : " + formatDateTime);

        System.out.println("After : " + formatDateTime.format(formatter));

        DateTimeFormatter ldtFormatter = DateTimeFormatter.ofPattern(fullReadableTSFormatString);
        LocalDateTime ldt = LocalDateTime.parse(readableTS, ldtFormatter);
        System.out.println("LocalDateTime RAW: " + ldt);

        datum = new Date(120, 0, 22, 9, 40); // deprecated, aber ich brauche ein Datum ohne Sekunden/Millisekunden
        System.out.println("Kurzes Date ohne Millisekunden: " + datum);
        readableTS = readableDf.format(datum);
        ldtFormatter = DateTimeFormatter.ofPattern(fullReadableTSFormatString);
        ldt = LocalDateTime.parse(readableTS, ldtFormatter);
        System.out.println("LocalDateTime RAW: " + ldt);

        Date testHeute = new Date();
        System.out.println("Ist "
                + datum
                + " vor "
                + testHeute
                + " : "
                + ComfortableDuration.istAvorB(ComfortableDuration.dateToLocalDate(datum),
                    ComfortableDuration.dateToLocalDate(testHeute)));

        LocalDate silvester2021 = LocalDate.of(2021, 12, 31);
        LocalDateTime geburtstag2021 = LocalDateTime.of(2021, 4, 19, 12, 34, 0, 0);
        hierUndHeute = LocalDate.now();
        genauJetzt = LocalDateTime.now();

        Duration zeitBisSilvester = Duration.between(
            ComfortableDuration.transformLocalDateToLocalDateTimeStartOfDay(hierUndHeute),
            ComfortableDuration.transformLocalDateToLocalDateTimeStartOfDay(silvester2021));
        System.out.println("Zeit bis Silvester: " + zeitBisSilvester);
        Duration zeitBisNeujahr = Duration.between((genauJetzt),
            ComfortableDuration.transformLocalDateToLocalDateTimeEndOfDay(silvester2021));
        System.out.println("Zeit bis Neujahr / Feuerwerk: " + zeitBisNeujahr);
        // die Duration in GANZEN Tagen ohne Komma
        long tage = zeitBisNeujahr.toDays();
        // Zieht man von der Duration die Tage ab, bleiben die Stunden, wenn man sie als toHours abfragt, als ganze Zahl
        // ohne Komma
        long stunden = zeitBisNeujahr.minus(tage, ChronoUnit.DAYS).toHours();
        // Zieht man von der Duration die Tage ab, bleiben die Minuten per toMinutes, von denen man dann die Stunden
        // (*60) abzieht um die restl. Minuten zu erhalten
        long minuten = zeitBisNeujahr.minus(tage, ChronoUnit.DAYS).toMinutes() - (stunden * 60);
        // Gleicher Vorgang für die Sekunden
        long sekunden = zeitBisNeujahr.getSeconds() - (tage * 24 * 60 * 60) - (stunden * 60 * 60) - (minuten * 60);
        // die Nanosekunden lassen sich direkt raus lesen
        long nanosekunden = zeitBisNeujahr.getNano();

        System.out.println("--> es sind daher in diesem Seuchenjahr noch "
                + tage
                + " Tage "
                + stunden
                + " Stunden "
                + minuten
                + " Minuten und "
                + sekunden
                + " Sekunden durch zu stehen.");
        System.out.println("    Und dann bleiben immer noch " + nanosekunden + " Nanosekunden zu durchleiden.");

        Duration zeitBis60 = Duration.between((genauJetzt), (geburtstag2021));
        System.out.println("Zeit bis SECHZIG: " + zeitBis60);

        ComfortableDuration cd = new ComfortableDuration(zeitBis60);
        System.out.println("--> es sind bis zum 60er noch "
                + cd.getTage()
                + " Tage "
                + cd.getStunden()
                + " Stunden "
                + cd.getMinuten()
                + " Minuten und "
                + cd.getSekunden()
                + " Sekunden durch zu stehen.");
        System.out
        .println("    Und dann bleiben immer noch " + ergebnis.getNanos() + " Nanosekunden zu warten.");

        System.out.println("Alternative Ausgabe (dank LinkedHashMap in sauberer Reihenfolge): ");
        for (Entry<String, Long> element : cd.getInhalt().entrySet()) {
            System.out.println(element.getKey() + " : " + element.getValue());
        }

        LocalDateTime heute = LocalDateTime.now();
        LocalDateTime morgen = heute.plusDays(1);
        Duration zeitBisMorgen = Duration.between((heute), (morgen));
        System.out.println("Zeit bis morgen: " + zeitBisMorgen);
        cd = new ComfortableDuration(zeitBisMorgen);
        for (Entry<String, Long> element : cd.getInhalt().entrySet()) {
            System.out.println(element.getKey() + " : " + element.getValue());
        }


        // Instant instMorgen = instJetzt.plus(1, ChronoUnit.DAYS);
        Instant instEnde = Instant.now();
        zeitBisMorgen = Duration.between((instJetzt), (instEnde));
        System.out.println("Zeit vergangen im Programm (via Instant): " + zeitBisMorgen);
        cd = new ComfortableDuration(zeitBisMorgen);
        for (Entry<String, Long> element : cd.getInhalt().entrySet()) {
            System.out.println(element.getKey() + " : " + element.getValue());
        }


        // ---
        System.out.println("------------------------------------------");
        Date tDate = new Date();
        System.out.println("Direkte Methode Date: "
                + tDate
                + " -> LocalDateTime: "
                + ComfortableDuration.dateToLocalDateTime(tDate, Locale.GERMAN, TimeZone.getTimeZone("Europe/Vienna")));

        System.out.println("Direkte Methode Date: "
                + tDate
                + " -> LocalDate: "
                + ComfortableDuration.dateToLocalDate(tDate, Locale.GERMAN, TimeZone.getTimeZone("Europe/Vienna")));

        datum = new Date();
        System.out.println("Heutiges Datum RAW: " + datum);
        System.out
        .println("Das wäre in CAN: " + ComfortableDuration.convertToLocalDateTimeViaInstant(datum, timezoneCC));

        System.out.println("********** FERTIG *************");
    }

}
