package jp.co.sss.lms.ct.f03_report;

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
import org.openqa.selenium.WebElement;

import jp.co.sss.lms.ct.util.WebDriverUtils;

/**
 * 結合テスト レポート機能
 * ケース07
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース07 受講生 レポート新規登録(日報) 正常系")
public class Case07 {

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
		}, "ケース07_No.01");

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
		}, "ケース07_No.02_1");

		loginButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertEquals(webDriver.getTitle(), "コース詳細 | LMS");

		getEvidence(new Object() {
		}, "ケース07_No.02_2");
	}

	@Test
	@Order(3)
	@DisplayName("テスト03 未提出の研修日の「詳細」ボタンを押下しセクション詳細画面に遷移")
	void test03() {
		List<WebElement> sectionList = webDriver.findElements(By.className("sctionList"));
		List<WebElement> tableRecords;
		// 処理管理フラグ
		Boolean flag = false;

		for (WebElement section : sectionList) {
			tableRecords = section.findElements(By.tagName("tr"));

			for (WebElement tr : tableRecords) {
				// trタグ内に「未提出」が存在すれば、「詳細」ボタンをクリック
				if (tr.findElements(By.xpath(".//span[(text())='未提出']")).size() != 0) {
					getEvidence(new Object() {
					}, "ケース07_No.03_1");

					WebElement detailButton = tr.findElement(By.className("btn"));
					scrollByElementAndOffset(detailButton, "-100");
					detailButton.click();

					visibilityTimeout(By.xpath("//h3[text() = '本日のレポート']"), 10);

					flag = true;
					break;
				}
			}
			if (flag) {
				break;
			}
		}

		assertEquals(webDriver.getTitle(), "セクション詳細 | LMS");
		getEvidence(new Object() {
		}, "ケース07_No.03_2");
	}

	@Test
	@Order(4)
	@DisplayName("テスト04 「提出する」ボタンを押下しレポート登録画面に遷移")
	void test04() {
		WebElement submissionButton = webDriver.findElement(By.xpath("//input[contains(@value,'日報')]"));

		getEvidence(new Object() {
		}, "ケース07_No.04_1");

		submissionButton.click();
		visibilityTimeout(By.xpath("//legend[text() = '報告レポート']"), 10);

		assertEquals(webDriver.getTitle(), "レポート登録 | LMS");
		getEvidence(new Object() {
		}, "ケース07_No.04_2");
	}

	@Test
	@Order(5)
	@DisplayName("テスト05 報告内容を入力して「提出する」ボタンを押下し確認ボタン名が更新される")
	void test05() {
		WebElement inputForm = webDriver.findElement(By.tagName("textarea"));
		WebElement submissionButton = webDriver.findElement(By.xpath("//button[contains(text(),'提出')]"));

		inputForm.clear();
		inputForm.sendKeys("テストケース07");

		getEvidence(new Object() {
		}, "ケース07_No.05_1");

		submissionButton.click();

		visibilityTimeout(By.xpath("//h3[text() = '本日のレポート']"), 10);

		WebElement submittedButton = webDriver.findElement(By.xpath("//input[contains(@value,'日報')]"));

		assertEquals(submittedButton.getAttribute("value"), "提出済み日報【デモ】を確認する");
		getEvidence(new Object() {
		}, "ケース07_No.05_2");
	}

}
