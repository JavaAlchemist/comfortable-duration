package at.diwh.comfortableduration.util;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
/**
 * Klasse, die im Konstruktor eine DURATION ({@link java.time.Duration}) übernimmt und danach einfache
 * Methoden bereitstellt, um mittels
 * <li>getTage()</li>
 * <li>getStunden()</li>
 * <li>getMinuten()</li>
 * <li>getSekunden()</li>
 * <li>getNanos()</li>
 * aus der Instanz die entsprechenden Werte raus zu lesen.
 * <br/> <b>Beispiel</b> für die Nützlichkeit: 
 * <br/>Eine "unleserliche" <i>Duration</i> im ISO8601-Format: PT1990H40M32.025S <br/>-> Tage : 82, Stunden : 22, Minuten : 40, Sekunden : 32, Nanosekunden : 25000000
 * @author diwh
 *
 */
public class ComfortableDuration {

    private Map<String, Long> inhalt = new LinkedHashMap<String, Long>();



    /**
     * Gibt eine Kopie der Map zurück, in der das Objekt die Duration-Werte speichert.
     * Da eine Kopie zurück gegeben wird, kann die Map innerhalb des Objekts nicht (unabsichtlich oder unbedacht) verändert werden.
     * D.h. ein getInhalt().put(...) hat keine Auswirkung auf den Inhalt im Objekt selbst. Oder anders: Jeder get-Aufrufe 
     * nach einem <i>new</i> liefern während der Lebensdauer (also bis zu einem anderen <i>new</i> des Objekts immer den gleichen Wert.
     * @return a copy of the Map
     */
    public  Map<String, Long> getInhalt() {
        return (new LinkedHashMap<String, Long>(inhalt));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, Long> element : inhalt.entrySet()) {
            builder.append(element.getKey() + " : [" + element.getValue()+ "]       ");
        }
        return builder.toString().trim();
    }


    /**
     * Extrahiert aus einer Duration im Format ISO8601 die Informationen in eine Map. Diese Map enthält Werte für folgende Keys 
     * <br/> Tage, Stunden, Minuten, Sekunden, Nanosekunden
     * <br/>
     * <br/> <b>Beispiel:</b> Man definiert zwei aufeinanderfolgende Tage mittels
     * <br/>  LocalDateTime heute = LocalDateTime.now();  LocalDateTime morgen = heute.plusDays(1);
     * <br/> und hat dann die Duration: Duration zeitBisMorgen = Duration.between((heute),(morgen)); 
     * <br/> ISO8601-Zeit bis morgen: PT24H wird zu 
     * <br/> Tage : 1, Stunden : 0, Minuten : 0, Sekunden : 0, Nanosekunden : 0
     * <br/>
     * <br/> Oder ein anderes <b>Beispiel</b>: 
     * <br/>"unleserliches" ISO8601: PT1990H40M32.025S <br/>-> Tage : 82, Stunden : 22, Minuten : 40, Sekunden : 32, Nanosekunden : 25000000
     * @param t - Duration
     * @return eine LinkedHashMap (damit die Reiheinfolge Tage, Stunden, Minuten, Sekunden, Nanosekunden beibehalten bleibt)
     */
    private static Map<String, Long> extractTageStundenMinutenSekundenNanos(Duration t) {
        Map<String, Long> result = new LinkedHashMap<String, Long>();
        // die Duration in GANZEN Tagen ohne Komma
        long tage = t.toDays();
        result.put("Tage", Long.valueOf(tage));
        // Zieht man von der Duration die Tage ab, bleiben die Stunden, wenn man sie als toHours abfragt, als ganze Zahl ohne Komma
        long stunden = t.minus(tage, ChronoUnit.DAYS).toHours();
        result.put("Stunden", Long.valueOf(stunden));
        // Zieht man von der Duration die Tage ab, bleiben die Minuten per toMinutes, von denen man dann die Stunden (*60) abzieht um die restl. Minuten zu erhalten        
        long minuten = t.minus(tage, ChronoUnit.DAYS).toMinutes() - (stunden*60); 
        result.put("Minuten", Long.valueOf(minuten));
        // Gleicher Vorgang für die Sekunden 
        long sekunden = t.minus(tage, ChronoUnit.DAYS).getSeconds() - (stunden*60*60) - (minuten*60);
        result.put("Sekunden", Long.valueOf(sekunden));
        // die Nanosekunden lassen sich direkt per getter raus lesen
        long nanosekunden = t.getNano(); 
        result.put("Nanosekunden", Long.valueOf(nanosekunden));
        return result;
    }

    /**
     * Testet, ob Zeitpunkt A vor dem Zeitpunkt B war. Wenn A und B den gleichen Zeitpunkt darstellen, ist A
     * <b>nicht</b> vor B. Wenn die Parameter nicht LocalDate oder LocalDateTime sind (oder wechselseitig
     * unterschiedlich) dann wird die Routine abstürzen. Das ist gewollt, denn dann hat man beim Aufruf ärgere Probleme
     * ignoriert und dann soll der Vergleich scheitern.
     * 
     * @param a ein LocalDate oder ein LocalDateTime
     * @param b ein LocalDate oder ein LocalDateTime
     * @return wahr, wenn der Zeitpunkt A vor dem Zeitpunkt B war
     */
    public static boolean istAvorB(Temporal a, Temporal b) {
        if (a instanceof LocalDate) {
            return (((LocalDate) a).isBefore((LocalDate) b));
        } else {
            return (((LocalDateTime) a).isBefore((LocalDateTime) b));
        }
    }

    /**
     * Testet, ob Zeitpunkt A nach dem Zeitpunkt B war. Wenn A und B den gleichen Zeitpunkt darstellen, ist A
     * <b>nicht</b> nach B. Wenn die Parameter nicht LocalDate oder LocalDateTime sind (oder wechselseitig
     * unterschiedlich) dann wird die Routine abstürzen. Das ist gewollt, denn dann hat man beim Aufruf ärgere Probleme
     * ignoriert und dann soll der Vergleich scheitern.
     * 
     * @param a ein LocalDate oder ein LocalDateTime
     * @param b ein LocalDate oder ein LocalDateTime
     * @return wahr, wenn der Zeitpunkt A nach dem Zeitpunkt B war
     */
    public static boolean istAnachB(Temporal a, Temporal b) {
        return istAvorB(b, a);
    }

    /**
     * Testet, ob Zeitpunkt A gleich dem Zeitpunkt B ist. Wenn die Parameter nicht LocalDate oder LocalDateTime sind
     * (oder wechselseitig unterschiedlich) dann wird die Routine abstürzen. Das ist gewollt, denn dann hat man beim
     * Aufruf ärgere Probleme ignoriert und dann soll der Vergleich scheitern.
     * 
     * @param a ein LocalDate oder ein LocalDateTime
     * @param b ein LocalDate oder ein LocalDateTime
     * @return wahr, wenn der Zeitpunkt A weder vor noch nach dem Zeitpunkt B war, also gleich sein muss
     */
    public static boolean istAgleichB(Temporal a, Temporal b) {
        return (!(istAvorB(a, b) || istAnachB(a, b))); // wenn weder noch, dann gleich
    }

    /**
     * Ein Intervall A {...} liegt INNERHALB von B [...] (also reourniert true) wenn <br/>
     * Die Intervalle gleich sind [{...}] äquivalent geschrieben {[...]} <br/>
     * Der Beginn von A gleich dem Beginn von B ist aber A vor B endet [{...}...] äquivalent {[...}...] <br/>
     * Der Beginn von A hinter dem Beginn von B ist und A und B zugleich enden [...{...}] äquivalent [...{...]} <br/>
     * Der Beginn von A hinter dem Beginn von B ist und das Ende von A vor dem Ende von B ist [...{...}...] <br/>
     * Ansonsten retorunliert es false. <br/>
     * Der Typ aller Intervallgrenzen muss gleich sein, es kann LocalDate oder LocalDateTime sein, aber alle vier
     * Parameter müssen typgleich sein.
     * 
     * @param beginnA Beginn Intervall A - nimmt LocalDate oder LocalDateTime an
     * @param endeA Ende Intervall A - nimmt LocalDate oder LocalDateTime an, muss aber gleich dem Typ von beginnA sein
     * @param beginnB Beginn Intervall B - nimmt LocalDate oder LocalDateTime an, muss aber gleich dem Typ von beginnA
     *            sein
     * @param endeB Ende Intervall B - nimmt LocalDate oder LocalDateTime an, muss aber gleich dem Typ von beginnA sein
     * @return wahr, wenn A innerhalb von B liegt
     */
    public static boolean istIntervallAinIntervallB(Temporal beginnA, Temporal endeA, Temporal beginnB,
        Temporal endeB) {
        // [{...}] äquivalent geschrieben {[...]}
        if (istAgleichB(beginnA, beginnB) && istAgleichB(endeA, endeB)) {
            return true; // beide Intervalle sind gleich
        }

        // [{...}...] äquivalent {[...}...]
        if (istAgleichB(beginnA, beginnB) && istAvorB(endeA, endeB)) {
            return true;
        }

        // [...{...}] äquivalent [...{...]}
        if (istAgleichB(endeA, endeB) && istAnachB(beginnA, beginnB)) {
            return true;
        }

        // [...{...}...] oder wenn das hier nicht wahr ist, ist es eben false, weil A nicht in B liegt
        return (istAnachB(beginnA, beginnB) && istAvorB(endeA, endeB));
    }

    /**
     * Wandelt ein Date um in ein LocalDateTime. z.B. ein Heutiges Datum RAW: Fri Jan 22 09:42:08 CET 2021 in
     * LocalDateTime RAW: 2021-01-22T09:42:08.295
     * 
     * @param datum - Date Objekt das umgewandelt werden soll
     * @param sprache - z.B. Locale aktuelleSprache = Locale.GERMAN;
     * @param ort - z.B. TimeZone timezone = TimeZone.getTimeZone("Europe/Vienna");
     * @return - ein Objekt vom Typ LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date datum, Locale sprache, TimeZone ort) {
        LocalDateTime ldt = null;
        final String fullReadableTSFormatString = "yyyy-MM-dd HH:mm:ss.SSS"; // Millisekunden, max. Auflösung von Date

        final SimpleDateFormat readableDf = new SimpleDateFormat(fullReadableTSFormatString, sprache);
        readableDf.setTimeZone(ort);
        final String readableTS = readableDf.format(datum);

        DateTimeFormatter ldtFormatter = DateTimeFormatter.ofPattern(fullReadableTSFormatString);
        ldt = LocalDateTime.parse(readableTS, ldtFormatter);

        return ldt;
    }

    /**
     * Wandelt ein Date in ein LocalDateTime; nimmt dazu als Zone die System-Zone
     * 
     * @param datum - Date Objekt
     * @return - ein LocalDateTime Objekt
     */
    public static LocalDateTime convertToLocalDateTimeViaInstant(Date datum) {
        return datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Wandelt ein Date in ein LocalDateTime; nimmt dazu als Zone die übergebene Zone (Ort)
     * 
     * @param datum - das Date
     * @param ort - Zone, also z.B. TimeZone.getTimeZone("Europe/Vienna")
     * @return - das LocalDateTime Objekt
     */
    public static LocalDateTime convertToLocalDateTimeViaInstant(Date datum, TimeZone ort) {
        return datum.toInstant().atZone(ort.toZoneId()).toLocalDateTime();
    }

    /**
     * Wandelt ein Date um in ein LocalDate. z.B. Date: Fri Jan 22 13:47:39 CET 2021 -> LocalDate: 2021-01-22 <br/>
     * Eine Liste der möglichen Werte für getTimeZone bekommt man mit String[] alleTimeZones =
     * TimeZone.getAvailableIDs();
     * 
     * @param datum - Date Objekt das umgewandelt werden soll
     * @param sprache - z.B. Locale aktuelleSprache = Locale.GERMAN;
     * @param ort - z.B. TimeZone timezone = TimeZone.getTimeZone("Europe/Vienna");
     * @return - ein Objekt vom Typ LocalDateTime
     */
    public static LocalDate dateToLocalDate(Date datum, Locale sprache, TimeZone ort) {
        LocalDate ldt = null;
        final String fullReadableTSFormatString = "yyyy-MM-dd"; // Nur Jahr-Monat-Tag

        final SimpleDateFormat readableDf = new SimpleDateFormat(fullReadableTSFormatString, sprache);
        readableDf.setTimeZone(ort);
        final String readableTS = readableDf.format(datum);

        DateTimeFormatter ldtFormatter = DateTimeFormatter.ofPattern(fullReadableTSFormatString);
        ldt = LocalDate.parse(readableTS, ldtFormatter);

        return ldt;
    }

    /**
     * Wandelt ein Date-Objekt in ein LocalDate um; nimmt die TimeZone vom System
     * 
     * @param datum - Date-Objekt
     * @return - ein LocalDate
     */
    public static LocalDate dateToLocalDate(Date datum) {
        return datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Wandelt ein Date-Objekt in ein LocalDate um; nimmt die TimeZone vom System
     * 
     * @param datum - Date-Objekt
     * @param ort - TimeZone, z.B. TimeZone.getTimeZone("Europe/Vienna")
     * @return - ein LocalDate
     */
    public static LocalDate dateToLocalDate(Date datum, TimeZone ort) {
        return datum.toInstant().atZone(ort.toZoneId()).toLocalDate();
    }

    /**
     * Wandelt ein LocalDateTime um in ein Date.
     * 
     * @param datum - DLocalDateTimeate Objekt das umgewandelt werden soll
     * @return - ein Objekt vom Typ Date
     */
    public static Date localDateTimeToDate(LocalDateTime datum) {
        return java.sql.Timestamp.valueOf(datum);
    }

    /**
     * Wandelt ein LocalDate um in ein Date.
     * 
     * @param datum - DLocalDateTimeate Objekt das umgewandelt werden soll
     * @return - ein Objekt vom Typ Date
     */
    public static Date localDateToDate(LocalDate datum) {
        return java.sql.Date.valueOf(datum);
    }

    /**
     * Macht aus einem LocalDate ein LocalDateTime und zwar zum Start des Tages. <br/>
     * D.h. aus z.B. 3.11.2020 wird 3.11.2020 um 0 Uhr 0 und 0 Nanosekunden
     * 
     * @param d LocalDate
     * @return ein LocalDateTime
     */
    public static LocalDateTime transformLocalDateToLocalDateTimeStartOfDay(LocalDate d) {
        LocalDateTime result = null;
        result = LocalDateTime.of(d.getYear(), d.getMonthValue(), d.getDayOfMonth(), 0, 0, 0, 0);
        return result;
    }

    /**
     * Macht aus einem LocalDate ein LocalDateTime und zwar zum ENDE des Tages. <br/>
     * D.h. aus z.B. 3.11.2020 wird 3.11.2020 um 23 Uhr 59 Minute, 59 Sekunden und 999 999 999 Nanosekunden
     * 
     * @param d LocalDate
     * @return ein LocalDateTime
     */
    public static LocalDateTime transformLocalDateToLocalDateTimeEndOfDay(LocalDate d) {
        LocalDateTime result = null;
        result = LocalDateTime.of(d.getYear(), d.getMonthValue(), d.getDayOfMonth(), 23, 59, 59, 999999999);
        return result;
    }

    /**
     * @param d - Duration
     */
    public ComfortableDuration(Duration d) {
        super();
        inhalt = extractTageStundenMinutenSekundenNanos(d);
    }


    @SuppressWarnings("unused")
    private ComfortableDuration() {
        super();
    }


    /**
     * @return Tage als Long
     */
    public Long getTage() {
        return inhalt.get("Tage");
    }

    /**
     * @return Stunden als Long
     */
    public Long getStunden() {
        return inhalt.get("Stunden");
    }

    /**
     * @return Minuten als Long
     */
    public Long getMinuten() {
        return inhalt.get("Minuten");
    }

    /**
     * @return Sekunden als Long
     */
    public Long getSekunden() {
        return inhalt.get("Sekunden");
    }

    /**
     * @return Nanosekunden als Long
     */
    public Long getNanos() {
        return inhalt.get("Nanosekunden");
    }
}


