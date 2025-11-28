package jp.co.sss.lms.ct.f05_exam;

import static jp.co.sss.lms.ct.util.WebDriverUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

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

import jp.co.sss.lms.ct.util.WebDriverUtils;

/**
 * 結合テスト 試験実施機能
 * 
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName(" 受講生 試験の実施 結果50点")
public class Case14 {

	/** テスト07およびテスト08 試験実施日時 */
	static Date date;

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
		}, "ケース14_No.01");

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
		}, "ケース14_No.02_1");

		loginButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertEquals(webDriver.getTitle(), "コース詳細 | LMS");

		getEvidence(new Object() {
		}, "ケース14_No.02_2");
	}

	@Test
	@Order(3)
	@DisplayName("テスト03 「試験有」の研修日の「詳細」ボタンを押下しセクション詳細画面に遷移")
	void test03() {
		// 「試験有」の研修日の要素を取得
		WebElement trainingRecord = webDriver.findElement(By.xpath("//span[contains(text(),'試験有')]/ancestor::tr[1]"));
		WebElement detailButton = trainingRecord.findElement(By.className("btn"));

		getEvidence(new Object() {
		}, "ケース14_No.03_1");

		detailButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertEquals(webDriver.getTitle(), "セクション詳細 | LMS");
		getEvidence(new Object() {
		}, "ケース14_No.03_2");
	}

	@Test
	@Order(4)
	@DisplayName("テスト04 「本日の試験」エリアの「詳細」ボタンを押下し試験開始画面に遷移")
	void test04() {
		WebElement detailButton = webDriver.findElement(By.xpath("//form[@action='/lms/exam/start']"));

		getEvidence(new Object() {
		}, "ケース14_No.04_1");

		detailButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertTrue(webDriver.getTitle().contains("試験"));
		getEvidence(new Object() {
		}, "ケース14_No.04_2");
	}

	@Test
	@Order(5)
	@DisplayName("テスト05 「試験を開始する」ボタンを押下し試験問題画面に遷移")
	void test05() {
		WebElement examButton = webDriver.findElement(By.xpath("//input[@value='試験を開始する']"));

		getEvidence(new Object() {
		}, "ケース14_No.05_1");

		examButton.click();

		visibilityTimeout(By.xpath("//form[@action='/lms/exam/start']"), 10);

		assertEquals(webDriver.findElement(By.id("backForm")).getAttribute("action"),
				"http://localhost:8080/lms/exam/start");
		getEvidence(new Object() {
		}, "ケース14_No.05_2");
	}

	@Test
	@Order(6)
	@DisplayName("テスト06 正答と誤答が半々で「確認画面へ進む」ボタンを押下し試験回答確認画面に遷移")
	void test06() throws InterruptedException {
		WebElement confirmButton = webDriver.findElement(By.xpath("//input[@value='確認画面へ進む']"));
		WebElement answerRow, answer;
		String xpath, answerIndex;

		for (int i = 1; i <= 6; i++) {
			xpath = "//div[contains(text(),'第" + i + "問')]";
			answerRow = webDriver.findElement(By.xpath(xpath));

			scrollByElement(answerRow);

			switch (i) {
			case 1:
				answerIndex = "answer-" + (i - 1) + "-2";
				break;
			case 2:
				answerIndex = "answer-" + (i - 1) + "-2";
				break;
			case 3:
				answerIndex = "answer-" + (i - 1) + "-0";
				break;
			case 4:
				answerIndex = "answer-" + (i - 1) + "-0";
				break;
			case 5:
				answerIndex = "answer-" + (i - 1) + "-1";
				break;
			case 6:
				answerIndex = "answer-" + (i - 1) + "-1";
				break;
			default:
				answerIndex = "";
			}

			answer = webDriver.findElement(By.id(answerIndex));
			answer.click();
			getEvidence(new Object() {
			}, "ケース14_No.06_" + i);
		}

		// 試験時間確保のため5秒待機
		Thread.sleep(5000);

		scrollByElement(confirmButton);
		confirmButton.click();

		visibilityTimeout(By.xpath("//form[@action='/lms/exam/result']"), 10);

		assertEquals(webDriver.getCurrentUrl(), "http://localhost:8080/lms/exam/answerCheck");
		getEvidence(new Object() {
		}, "ケース14_No.06_7");
	}

	@Test
	@Order(7)
	@DisplayName("テスト07 「回答を送信する」ボタンを押下し試験結果画面に遷移")
	void test07() throws InterruptedException {
		WebElement sendButton = webDriver.findElement(By.id("sendButton"));

		scrollByElement(sendButton);

		getEvidence(new Object() {
		}, "ケース14_No.07_1");

		// 試験時間確保のため5秒待機
		Thread.sleep(5000);
		sendButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//small[contains(text(),'あなたのスコア')]"), 10);

		assertEquals(webDriver.getCurrentUrl(), "http://localhost:8080/lms/exam/result");
		getEvidence(new Object() {
		}, "ケース14_No.07_2");
	}

	@Test
	@Order(8)
	@DisplayName("テスト08 「戻る」ボタンを押下し試験開始画面に遷移後当該試験の結果が反映される")
	void test08() {
		WebElement backButton = webDriver.findElement(By.xpath("//input[@value='戻る']"));

		scrollByElement(backButton);

		getEvidence(new Object() {
		}, "ケース14_No.08_1");

		backButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.className("active"), 10);

		assertTrue(webDriver.getTitle().contains("試験"));
		getEvidence(new Object() {
		}, "ケース14_No.08_2");
	}

}
