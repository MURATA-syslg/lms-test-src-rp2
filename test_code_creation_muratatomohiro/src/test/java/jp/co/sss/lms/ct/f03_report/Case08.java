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
import org.openqa.selenium.support.ui.Select;

import jp.co.sss.lms.ct.util.WebDriverUtils;

/**
 * 結合テスト レポート機能
 * ケース08
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース08 受講生 レポート修正(週報) 正常系")
public class Case08 {

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
		}, "ケース08_No.01");

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
		}, "ケース08_No.02_1");

		loginButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertEquals(webDriver.getTitle(), "コース詳細 | LMS");

		getEvidence(new Object() {
		}, "ケース08_No.02_2");
	}

	@Test
	@Order(3)
	@DisplayName("テスト03 提出済の研修日の「詳細」ボタンを押下しセクション詳細画面に遷移")
	void test03() {
		// 週報提出済みの日付要素のテーブルデータを取得
		WebElement days = webDriver.findElement(By.xpath("//td[text() = '2022年10月2日(日)']"));
		// 取得したテーブルデータの親要素(テーブルレコード)を取得
		WebElement section = days.findElement(By.xpath(".."));
		// 該当のテーブルレコード要素内から詳細ボタン要素を取得
		WebElement detailButton = section.findElement(By.className("btn"));

		scrollByElementAndOffset(detailButton, "-100");

		getEvidence(new Object() {
		}, "ケース08_No.03_1");

		detailButton.click();

		visibilityTimeout(By.xpath("//h3[text() = '本日のレポート']"), 10);

		assertEquals(webDriver.getTitle(), "セクション詳細 | LMS");
		getEvidence(new Object() {
		}, "ケース08_No.03_2");

	}

	@Test
	@Order(4)
	@DisplayName("テスト04 「確認する」ボタンを押下しレポート登録画面に遷移")
	void test04() {
		WebElement submittedButton = webDriver.findElement(By.xpath("//input[contains(@value,'週報')]"));

		scrollByElementAndOffset(submittedButton, "-100");

		getEvidence(new Object() {
		}, "ケース08_No.04_1");

		submittedButton.click();

		visibilityTimeout(By.xpath("//legend[text() = '報告レポート']"), 10);

		assertEquals(webDriver.getTitle(), "レポート登録 | LMS");
		getEvidence(new Object() {
		}, "ケース08_No.04_2");

	}

	@Test
	@Order(5)
	@DisplayName("テスト05 報告内容を修正して「提出する」ボタンを押下しセクション詳細画面に遷移")
	void test05() throws InterruptedException {
		WebElement learningTopic = webDriver.findElement(By.xpath("//label[contains(text(),'学習項目')]"));
		WebElement inputForm = learningTopic.findElement(By.xpath("//input[@type='text']"));
		Select selectBox = new Select(learningTopic.findElement(By.xpath("//select[@class='form-control']")));
		WebElement submissionButton = webDriver.findElement(By.xpath("//button[contains(text(),'提出')]"));
		List<WebElement> textareas = webDriver.findElements(By.tagName("textarea"));
		int index = 1;

		getEvidence(new Object() {
		}, "ケース08_No.05_" + index);
		index++;

		inputForm.clear();
		inputForm.sendKeys("テストケース08");

		getEvidence(new Object() {
		}, "ケース08_No.05_" + index);
		index++;

		selectBox.selectByIndex(1);

		getEvidence(new Object() {
		}, "ケース08_No.05_" + index);
		index++;

		// すべてのテキストエリアでテストを実施
		for (WebElement textarea : textareas) {
			scrollByElementAndOffset(textarea, "-50");

			// 「目標の達成度」項目かどうか
			if (textarea.getAttribute("id").equals("content_0")) {
				textarea.clear();

				getEvidence(new Object() {
				}, "ケース08_No.05_" + index);
				index++;

				textarea.sendKeys("10");

				getEvidence(new Object() {
				}, "ケース08_No.05_" + index);
				index++;
			} else {

				textarea.clear();

				getEvidence(new Object() {
				}, "ケース08_No.05_" + index);
				index++;

				textarea.sendKeys("1\nテストケース08\n!\"#$%$&’()?");

				getEvidence(new Object() {
				}, "ケース08_No.05_" + index);
				index++;
			}
		}

		submissionButton.click();

		visibilityTimeout(By.xpath("//h3[text() = '本日のレポート']"), 10);

		WebElement submittedButton = webDriver.findElement(By.xpath("//input[contains(@value,'週報')]"));

		scrollByElementAndOffset(submittedButton, "-50");

		assertEquals(submittedButton.getAttribute("value"), "提出済み週報【デモ】を確認する");
		getEvidence(new Object() {
		}, "ケース08_No.05_" + index);
		index++;
	}

	@Test
	@Order(6)
	@DisplayName("テスト06 上部メニューの「ようこそ○○さん」リンクからユーザー詳細画面に遷移")
	void test06() {
		WebElement linkElement = webDriver.findElement(By.xpath("//small[contains(text(),'ようこそ')]"));

		getEvidence(new Object() {
		}, "ケース08_No.06_1");

		linkElement.click();

		visibilityTimeout(By.xpath("//h2[text() = 'ユーザー詳細']"), 10);

		getEvidence(new Object() {
		}, "ケース08_No.06_2");

	}

	@Test
	@Order(7)
	@DisplayName("テスト07 該当レポートの「詳細」ボタンを押下しレポート詳細画面で修正内容が反映される")
	void test07() {
		// 週報提出済みの日付要素のテーブルデータを取得
		WebElement days = webDriver.findElement(By.xpath("//td[text() = '2022年10月2日(日)']"));
		// 取得したテーブルデータの親要素(テーブルレコード)を取得
		WebElement section = days.findElement(By.xpath(".."));
		// 該当のテーブルレコード要素内から詳細ボタン要素を取得
		WebElement detailButton = section.findElement(By.xpath(".//form[@action='/lms/report/detail']"));

		scrollByElementAndOffset(detailButton, "-50");

		getEvidence(new Object() {
		}, "ケース08_No.07_1");

		detailButton.click();

		visibilityTimeout(By.xpath("//h2[text() = '週報【デモ】 ']"), 10);

		assertEquals(webDriver.getTitle(), "レポート詳細 | LMS");
		getEvidence(new Object() {
		}, "ケース08_No.07_2");
	}

}
