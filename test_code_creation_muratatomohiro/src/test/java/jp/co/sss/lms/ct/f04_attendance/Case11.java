package jp.co.sss.lms.ct.f04_attendance;

import static jp.co.sss.lms.ct.util.WebDriverUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import jp.co.sss.lms.ct.util.WebDriverUtils;

/**
 * 結合テスト 勤怠管理機能
 * ケース11
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース11 受講生 勤怠直接編集 正常系")
public class Case11 {

	/** 前処理 */
	@BeforeAll
	static void before() {
		createDriver();
	}

	/** 後処理 */
	@AfterAll
	static void after() {
		closeDriver();
	}

	@Test
	@Order(1)
	@DisplayName("テスト01 トップページURLでアクセス")
	void test01() {
		goTo("http://localhost:8080/lms/");

		getEvidence(new Object() {
		}, "ケース11_No.01");

		assertEquals(webDriver.getTitle(), "ログイン | LMS");
		assertEquals(webDriver.findElement(By.className("btn")).getAttribute("value"), "ログイン");
		assertEquals(webDriver.getCurrentUrl(), "http://localhost:8080/lms/");
	}

	@Test
	@Order(2)
	@DisplayName("テスト02 初回ログイン済みの受講生ユーザーでログイン")
	void test02() {
		// ログインに必要な要素の取得
		WebElement userID = WebDriverUtils.webDriver.findElement(By.id("loginId"));
		WebElement password = WebDriverUtils.webDriver.findElement(By.id("password"));
		WebElement loginButton = WebDriverUtils.webDriver.findElement(By.cssSelector("[value='ログイン']"));
		userID.clear();
		userID.sendKeys("StudentAA01");
		password.clear();
		password.sendKeys("Password01");

		getEvidence(new Object() {
		}, "ケース11_No.02_1");

		loginButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertEquals(webDriver.getTitle(), "コース詳細 | LMS");

		getEvidence(new Object() {
		}, "ケース11_No.02_2");
	}

	@Test
	@Order(3)
	@DisplayName("テスト03 上部メニューの「勤怠」リンクから勤怠管理画面に遷移")
	void test03() {
		WebElement attendanceLink = webDriver.findElement(By.linkText("勤怠"));

		getEvidence(new Object() {
		}, "ケース11_No.03_1");

		attendanceLink.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		assertEquals(webDriver.getTitle(), "勤怠情報変更｜LMS");
		getEvidence(new Object() {
		}, "ケース11_No.03_2");

	}

	@Test
	@Order(4)
	@DisplayName("テスト04 「勤怠情報を直接編集する」リンクから勤怠情報直接変更画面に遷移")
	void test04() {
		WebElement attendanceLink = webDriver.findElement(By.linkText("勤怠情報を直接編集する"));

		getEvidence(new Object() {
		}, "ケース11_No.03_1");

		attendanceLink.click();

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		assertEquals(webDriver.getTitle(), "勤怠情報変更｜LMS");
		getEvidence(new Object() {
		}, "ケース11_No.03_2");
	}

	@Test
	@Order(5)
	@DisplayName("テスト05 すべての研修日程の勤怠情報を正しく更新し勤怠管理画面に遷移")
	void test05() {
		webDriver.manage().window().maximize();
		// すべての研修日程の勤怠入力欄を取得
		List<WebElement> attendances = webDriver.findElements(By.xpath("//tbody[@class='db']/tr"));
		Select startHour, startMinute, endHour, endMinute;

		WebElement submitButton = webDriver.findElement(By.name("complete"));

		getEvidence(new Object() {
		}, "ケース11_No.04_1");

		for (WebElement attendance : attendances) {
			// 出勤時間(Hour)の設定
			startHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startHour')]")));
			startHour.selectByIndex(10);
			// 出勤時間(Minute)の設定
			startMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startMinute')]")));
			startMinute.selectByIndex(1);

			// 退勤時間(Hour)の設定
			endHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endHour')]")));
			endHour.selectByIndex(19);
			// 退勤時間(Minute)の設定
			endMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endMinute')]")));
			endMinute.selectByIndex(1);

			assertEquals(startHour.getFirstSelectedOption().getAttribute("value"), "9");
			assertEquals(startMinute.getFirstSelectedOption().getAttribute("value"), "0");
			assertEquals(endHour.getFirstSelectedOption().getAttribute("value"), "18");
			assertEquals(endMinute.getFirstSelectedOption().getAttribute("value"), "0");
		}

		getEvidence(new Object() {
		}, "ケース11_No.04_2");

		scrollByElement(submitButton);
		submitButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		getEvidence(new Object() {
		}, "ケース11_No.04_3");
	}

}
