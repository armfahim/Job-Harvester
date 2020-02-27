/*
 * package io.naztech.jobharvestar.scraper;
 * 
 * import java.io.File; import java.io.FileOutputStream; import
 * java.text.SimpleDateFormat; import java.util.ArrayList; import
 * java.util.Date; import java.util.List; import java.util.concurrent.TimeUnit;
 * 
 * import org.apache.poi.hssf.usermodel.HeaderFooter; import
 * org.apache.poi.ss.usermodel.Footer; import
 * org.apache.poi.xssf.usermodel.XSSFSheet; import
 * org.apache.poi.xssf.usermodel.XSSFWorkbook; import org.junit.AfterClass;
 * import org.junit.Before; import org.junit.BeforeClass; import org.junit.Test;
 * import org.openqa.selenium.By; import org.openqa.selenium.WebElement; import
 * org.openqa.selenium.chrome.ChromeDriver; import
 * org.openqa.selenium.chrome.ChromeDriverService; import
 * org.openqa.selenium.chrome.ChromeOptions; import
 * org.openqa.selenium.support.ui.ExpectedConditions; import
 * org.openqa.selenium.support.ui.WebDriverWait;
 * 
 * public class MedicleTest { // public static String URL =
 * "https://www.hospitalsafetygrade.org/h/seton-medical-center-austin?findBy=hospital&hospital"
 * // + "=SETON+MEDICAL+CENTER+AUSTIN&rPos=125&rSort=grade";
 * 
 * public static String URL =
 * "https://www.hospitalsafetygrade.org/h/st-davids-north-austin-medical-center?findBy=hospital&hospital="
 * + "ST.+DAVID%27S+NORTH+AUSTIN+MEDICAL+CENTER&rPos=100&rSort=grade";
 * 
 * 
 * public static ChromeDriver driver; public static WebDriverWait wait;
 * 
 * public static int cellInRow = 0; public static String Grade1 = "a"; public
 * static String Grade2 = "b"; public static String Grade3 = "c";
 * 
 * @BeforeClass public static void setUpBeforeClass() throws Exception { driver
 * = getChromeDriver(true); wait = new WebDriverWait(driver, 10); }
 * 
 * @AfterClass public static void tearDownAfterClass() throws Exception { }
 * 
 * @Before public void setUp() throws Exception { }
 * 
 * @Test public void test() { driver.get(URL); wait = new WebDriverWait(driver,
 * 100); WebElement detailTableUrl = wait
 * .until(ExpectedConditions.presenceOfElementLocated(By.xpath(
 * "//p[@class='detailedTableView']/a"))); WebElement currentGrade =
 * driver.findElement(By.xpath("//img[@alt='Grade a']")); WebElement
 * currentSession = driver.findElementByClassName("date"); WebElement click =
 * driver.findElement(By.xpath("//div[@class='pastGradesWrapper']/div/a"));
 * click.click(); WebElement previousGrade =
 * driver.findElement(By.xpath("//img[@alt='Grade a']")); WebElement
 * previousGrade2 = driver.findElement(By.xpath("//img[@alt='Grade b']"));
 * 
 * WebElement yearSet =
 * driver.findElement(By.xpath("//div[@class='pastGrades']/div[1]"));
 * List<WebElement> previousSession =
 * yearSet.findElements(By.className("date"));
 * 
 * List<String> gradeList = new ArrayList<>();
 * gradeList.add(currentGrade.getAttribute("alt"));
 * gradeList.add(currentSession.getText());
 * gradeList.add(previousGrade.getAttribute("alt"));
 * gradeList.add(previousSession.get(0).getText());
 * gradeList.add(previousGrade2.getAttribute("alt"));
 * gradeList.add(previousSession.get(1).getText());
 * 
 * System.out.println(detailTableUrl.getAttribute("href")); try {
 * getDetails(detailTableUrl.getAttribute("href"), gradeList); } catch
 * (Exception e) { e.printStackTrace(); }
 * 
 * }
 * 
 * public void getDetails(String url, List<String> gradeList) throws Exception {
 * String left, center, right; driver.get(url); wait = new WebDriverWait(driver,
 * 100); List<WebElement> header = wait.until(ExpectedConditions
 * .presenceOfAllElementsLocatedBy(By.xpath(
 * "//table[@class='outcomesTable']/tbody/tr/th"))); List<WebElement> data =
 * wait.until(ExpectedConditions .presenceOfAllElementsLocatedBy(By.xpath(
 * "//table[@class='outcomesTable']/tbody/tr/td"))); List<WebElement> data2 =
 * wait.until(ExpectedConditions .presenceOfAllElementsLocatedBy(By.xpath(
 * "//table[@class='processTable']/tbody/tr/td"))); List<String> headerList =
 * new ArrayList<>(); data.addAll(data2);
 * 
 * for (WebElement el : header) { headerList.add(el.getText()); }
 * 
 * List<String> dataList = new ArrayList<>(); for (WebElement el : data) {
 * dataList.add(el.getText()); }
 * 
 * for (String el : gradeList) { dataList.add(el); }
 * 
 * File file = new
 * File("C:\\Users\\fahim.reza\\Desktop\\TRACK_SQL\\St.David'sNorthAustin.xlsx")
 * ; XSSFWorkbook wb = new XSSFWorkbook(); XSSFSheet sh =
 * wb.createSheet("Sheet1"); // XSSFSheet sh2 = wb.createSheet("Sheet2");
 * 
 * cellInRow = header.size();
 * 
 * extractData(headerList, sh, -1); extractData(dataList, sh, 0);
 * 
 * Footer footer = sh.getFooter();
 * 
 * left = "GRADE A"; center = "GRADE B"; right = "GRADE C";
 * footer.setCenter(center); footer.setLeft(left); footer.setRight(right);
 * SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 * footer.setLeft(dateFormat.format(new Date())); footer.setRight("Page " +
 * HeaderFooter.page() + " of " + HeaderFooter.numPages());
 * 
 * // extractData(header, sh2, -1); // extractData(data, sh2, 0);
 * 
 * writeFile(file, wb);
 * 
 * wb.close(); }
 * 
 * public void extractData(List<String> dataList, XSSFSheet sh, int row) {
 * 
 * for (int i = 0; i < dataList.size(); i++) { String text = dataList.get(i); if
 * (i % cellInRow == 0) { sh.createRow(++row).createCell(0).setCellValue(text);
 * } else { sh.getRow(row).createCell(i % cellInRow).setCellValue(text); } } }
 * 
 * public void writeFile(File file, XSSFWorkbook wb) { try { FileOutputStream
 * fos = new FileOutputStream(file); wb.write(fos); } catch (Exception e) {
 * e.printStackTrace(); } }
 * 
 * public static ChromeDriver getChromeDriver(boolean isHeadless) {
 * ChromeDriverService service = new ChromeDriverService.Builder()
 * .usingDriverExecutable(new
 * File("webdrivers/chromedriver-76.exe")).usingAnyFreePort().build();
 * 
 * ChromeDriver driver = new ChromeDriver(service, new
 * ChromeOptions().setHeadless(true));
 * 
 * Developer should increase page load timeout in their scraper class when
 * needed
 * 
 * driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
 * driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS); return
 * driver; } }
 */