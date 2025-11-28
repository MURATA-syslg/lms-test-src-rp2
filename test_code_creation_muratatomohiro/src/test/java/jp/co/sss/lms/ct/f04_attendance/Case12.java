package jp.co.sss.lms.ct.f04_attendance;

import static jp.co.sss.lms.ct.util.WebDriverUtils.*;
import static org.junit.jupiter.api.Assertions.*;

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
 * ケース12
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース12 受講生 勤怠直接編集 入力チェック")
public class Case12 {

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
		}, "ケース12_No.01");

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
		}, "ケース12_No.02_1");

		loginButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertEquals(webDriver.getTitle(), "コース詳細 | LMS");

		getEvidence(new Object() {
		}, "ケース12_No.02_2");
	}

	@Test
	@Order(3)
	@DisplayName("テスト03 上部メニューの「勤怠」リンクから勤怠管理画面に遷移")
	void test03() {
		WebElement attendanceLink = webDriver.findElement(By.linkText("勤怠"));

		getEvidence(new Object() {
		}, "ケース12_No.03_1");

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
		}, "ケース12_No.03_2");

	}

	@Test
	@Order(4)
	@DisplayName("テスト04 「勤怠情報を直接編集する」リンクから勤怠情報直接変更画面に遷移")
	void test04() {
		WebElement attendanceLink = webDriver.findElement(By.linkText("勤怠情報を直接編集する"));

		getEvidence(new Object() {
		}, "ケース12_No.03_1");

		attendanceLink.click();

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		assertEquals(webDriver.getTitle(), "勤怠情報変更｜LMS");
		getEvidence(new Object() {
		}, "ケース12_No.03_2");
	}

	@Test
	@Order(5)
	@DisplayName("テスト05 不適切な内容で修正してエラー表示：出退勤の（時）と（分）のいずれかが空白")
	void test05() {
		webDriver.manage().window().maximize();
		// 先頭の研修日程の勤怠入力欄を取得
		WebElement attendance = webDriver.findElement(By.xpath("//tbody[@class='db']/tr[1]"));
		Select startHour, startMinute, endHour, endMinute;

		WebElement submitButton = webDriver.findElement(By.name("complete"));

		getEvidence(new Object() {
		}, "ケース12_No.05_1");

		// 出勤時間(Hour)の設定
		startHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startHour')]")));
		startHour.selectByIndex(10);
		// 出勤時間(Minute)の設定
		startMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startMinute')]")));
		startMinute.selectByIndex(0);

		// 退勤時間(Hour)の設定
		endHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endHour')]")));
		endHour.selectByIndex(0);
		// 退勤時間(Minute)の設定
		endMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endMinute')]")));
		endMinute.selectByIndex(1);

		getEvidence(new Object() {
		}, "ケース12_No.05_2");

		scrollByElement(submitButton);
		submitButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		WebElement errorMsg = webDriver.findElement(By.xpath("//span[contains(text(),'出勤時間')]"));
		assertTrue(errorMsg.getText().contains("出勤時間が正しく入力されていません。"));

		errorMsg = webDriver.findElement(By.xpath("//span[contains(text(),'退勤時間')]"));
		assertTrue(errorMsg.getText().contains("退勤時間が正しく入力されていません。"));

		getEvidence(new Object() {
		}, "ケース12_No.05_3");
	}

	@Test
	@Order(6)
	@DisplayName("テスト06 不適切な内容で修正してエラー表示：出勤が空白で退勤に入力あり")
	void test06() {
		webDriver.manage().window().maximize();
		// 先頭の研修日程の勤怠入力欄を取得
		WebElement attendance = webDriver.findElement(By.xpath("//tbody[@class='db']/tr[1]"));
		Select startHour, startMinute, endHour, endMinute;

		WebElement submitButton = webDriver.findElement(By.name("complete"));

		getEvidence(new Object() {
		}, "ケース12_No.06_1");

		// 出勤時間(Hour)の設定
		startHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startHour')]")));
		startHour.selectByIndex(0);
		// 出勤時間(Minute)の設定
		startMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startMinute')]")));
		startMinute.selectByIndex(0);

		// 退勤時間(Hour)の設定
		endHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endHour')]")));
		endHour.selectByIndex(19);
		// 退勤時間(Minute)の設定
		endMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endMinute')]")));
		endMinute.selectByIndex(1);

		getEvidence(new Object() {
		}, "ケース12_No.06_2");

		scrollByElement(submitButton);
		submitButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		WebElement errorMsg = webDriver.findElement(By.xpath("//span[contains(@class,'help-inline')]"));
		assertTrue(errorMsg.getText().contains("出勤情報がないため退勤情報を入力出来ません。"));

		getEvidence(new Object() {
		}, "ケース12_No.06_3");
	}

	@Test
	@Order(7)
	@DisplayName("テスト07 不適切な内容で修正してエラー表示：出勤が退勤よりも遅い時間")
	void test07() {
		webDriver.manage().window().maximize();
		// 先頭の研修日程の勤怠入力欄を取得
		WebElement attendance = webDriver.findElement(By.xpath("//tbody[@class='db']/tr[1]"));
		Select startHour, startMinute, endHour, endMinute;

		WebElement submitButton = webDriver.findElement(By.name("complete"));

		getEvidence(new Object() {
		}, "ケース12_No.07_1");

		// 出勤時間(Hour)の設定
		startHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startHour')]")));
		startHour.selectByIndex(22);
		// 出勤時間(Minute)の設定
		startMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startMinute')]")));
		startMinute.selectByIndex(1);

		// 退勤時間(Hour)の設定
		endHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endHour')]")));
		endHour.selectByIndex(19);
		// 退勤時間(Minute)の設定
		endMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endMinute')]")));
		endMinute.selectByIndex(1);

		getEvidence(new Object() {
		}, "ケース12_No.07_2");

		scrollByElement(submitButton);
		submitButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		WebElement errorMsg = webDriver.findElement(By.xpath("//span[contains(@class,'help-inline')]"));
		assertTrue(errorMsg.getText().contains("退勤時刻[0]は出勤時刻[0]より後でなければいけません。"));

		getEvidence(new Object() {
		}, "ケース12_No.07_3");
	}

	@Test
	@Order(8)
	@DisplayName("テスト08 不適切な内容で修正してエラー表示：出退勤時間を超える中抜け時間")
	void test08() {
		webDriver.manage().window().maximize();
		// 先頭の研修日程の勤怠入力欄を取得
		WebElement attendance = webDriver.findElement(By.xpath("//tbody[@class='db']/tr[1]"));
		Select startHour, startMinute, endHour, endMinute, blankTime;

		WebElement submitButton = webDriver.findElement(By.name("complete"));

		getEvidence(new Object() {
		}, "ケース12_No.08_1");

		// 出勤時間(Hour)の設定
		startHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startHour')]")));
		startHour.selectByIndex(10);
		// 出勤時間(Minute)の設定
		startMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'startMinute')]")));
		startMinute.selectByIndex(1);

		// 退勤時間(Hour)の設定
		endHour = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endHour')]")));
		endHour.selectByIndex(11);
		// 退勤時間(Minute)の設定
		endMinute = new Select(attendance.findElement(By.xpath(".//select[contains(@id,'endMinute')]")));
		endMinute.selectByIndex(1);

		// 中抜け時間の設定
		blankTime = new Select(attendance.findElement(By.xpath(".//select[contains(@name,'blankTime')]")));
		blankTime.selectByValue("300");

		getEvidence(new Object() {
		}, "ケース12_No.08_2");

		scrollByElement(submitButton);
		submitButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		WebElement errorMsg = webDriver.findElement(By.xpath("//span[contains(@class,'help-inline')]"));
		assertTrue(errorMsg.getText().contains("中抜け時間が勤務時間を超えています。"));

		getEvidence(new Object() {
		}, "ケース12_No.08_3");

	}

	@Test
	@Order(9)
	@DisplayName("テスト09 不適切な内容で修正してエラー表示：備考が100文字超")
	void test09() {
		webDriver.manage().window().maximize();
		// 先頭の研修日程の勤怠入力欄を取得
		WebElement attendance = webDriver.findElement(By.xpath("//tbody[@class='db']/tr[1]"));
		WebElement note = attendance.findElement(By.xpath(".//input[contains(@name,'note')]"));
		Select startHour, startMinute, endHour, endMinute;

		WebElement submitButton = webDriver.findElement(By.name("complete"));

		getEvidence(new Object() {
		}, "ケース12_No.09_1");

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

		// 備考欄に「a」を101文字入力
		note.sendKeys("a".repeat(101));

		getEvidence(new Object() {
		}, "ケース12_No.09_2");

		scrollByElement(submitButton);
		submitButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		WebElement errorMsg = webDriver.findElement(By.xpath("//span[contains(@class,'help-inline')]"));
		assertTrue(errorMsg.getText().contains("備考の長さが最大値(100)を超えています。"));

		getEvidence(new Object() {
		}, "ケース12_No.09_3");
	}

}
