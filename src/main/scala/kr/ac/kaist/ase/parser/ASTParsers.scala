package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.model.Script
import kr.ac.kaist.ase.util.Useful._
import scala.util.matching.Regex
import scala.util.parsing.combinator._
import scala.util.parsing.input._

trait ASTParsers extends RegexParsers {
  private val self = this

  // not skip white sapces
  override def skipWhitespace = false

  // lookahead
  implicit def lookaheadSyntax[A](parser: => Parser[A]): LookaheadSyntax[A] = new LookaheadSyntax[A](parser)
  class LookaheadSyntax[A](parser: => Parser[A]) {
    def unary_-(): Parser[Unit] = Parser { in =>
      parser(in) match {
        case Success(_, _) => Failure("Wrong Lookahead", in)
        case _ => Success((), in)
      }
    }
    def unary_+(): Parser[Unit] = Parser { in =>
      parser(in) match {
        case s @ Success(_, _) => Success((), in)
        case _ => Failure("Wrong Lookahead", in)
      }
    }
  }

  lazy val STR_MATCH: Parser[String] = ""
  lazy val STR_MISMATCH: Parser[Nothing] = failure("")

  // but not
  implicit def butnotSyntax(parser: => Parser[String]): ButnotSyntax = new ButnotSyntax(parser)
  class ButnotSyntax(parser: => Parser[String]) {
    def \(cond: => Parser[String]): Parser[String] = {
      parser.filter(s => parseAll(cond, s).isEmpty)
    }
  }

  // special characters
  lazy val ZWNJ: Parser[String] = "\u200C"
  lazy val ZWJ: Parser[String] = "\u200D"
  lazy val ZWNBSP: Parser[String] = "\uFEFF"

  lazy val TAB: Parser[String] = "\u0009"
  lazy val VT: Parser[String] = "\u000B"
  lazy val FF: Parser[String] = "\u000C"
  lazy val SP: Parser[String] = "\u0020"
  lazy val NBSP: Parser[String] = "\u00A0"
  lazy val USP: Parser[String] = "[\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000]".r

  lazy val LF: Parser[String] = "\u000A"
  lazy val CR: Parser[String] = "\u000D"
  lazy val LS: Parser[String] = "\u2028"
  lazy val PS: Parser[String] = "\u2029"

  lazy val WhiteSpace: Parser[String] = TAB | VT | FF | SP | NBSP | ZWNBSP | USP
  lazy val LineTerminator: Parser[String] = LF | CR | LS | PS
  lazy val LineTerminatorSequence: Parser[String] = LF | CR <~ -LF | LS | PS | seq(CR, LF)
  lazy val lines: Regex = "[\u000A\u000D\u2028\u2029]".r

  lazy val Unicode: Parser[String] = "(?s).".r
  lazy val IDStart: Parser[String] = """[A-Za-zªµºÀ-ÖØ-öø-ˁˆ-ˑˠ-ˤˬˮͰ-ʹͶͷͺ-ͽͿΆΈ-ΊΌΎ-ΡΣ-ϵϷ-ҁҊ-ԯԱ-Ֆՙՠ-ֈא-תׯ-ײؠ-يٮٯٱ-ۓەۥۦۮۯۺ-ۼۿܐܒ-ܯݍ-ޥޱߊ-ߪߴߵߺࠀ-ࠕࠚࠤࠨࡀ-ࡘࡠ-ࡪࢠ-ࢴࢶ-ࢽऄ-हऽॐक़-ॡॱ-ঀঅ-ঌএঐও-নপ-রলশ-হঽৎড়ঢ়য়-ৡৰৱৼਅ-ਊਏਐਓ-ਨਪ-ਰਲਲ਼ਵਸ਼ਸਹਖ਼-ੜਫ਼ੲ-ੴઅ-ઍએ-ઑઓ-નપ-રલળવ-હઽૐૠૡૹଅ-ଌଏଐଓ-ନପ-ରଲଳଵ-ହଽଡ଼ଢ଼ୟ-ୡୱஃஅ-ஊஎ-ஐஒ-கஙசஜஞடணதந-பம-ஹௐఅ-ఌఎ-ఐఒ-నప-హఽౘ-ౚౠౡಀಅ-ಌಎ-ಐಒ-ನಪ-ಳವ-ಹಽೞೠೡೱೲഅ-ഌഎ-ഐഒ-ഺഽൎൔ-ൖൟ-ൡൺ-ൿඅ-ඖක-නඳ-රලව-ෆก-ะาำเ-ๆກຂຄຆ-ຊຌ-ຣລວ-ະາຳຽເ-ໄໆໜ-ໟༀཀ-ཇཉ-ཬྈ-ྌက-ဪဿၐ-ၕၚ-ၝၡၥၦၮ-ၰၵ-ႁႎႠ-ჅჇჍა-ჺჼ-ቈቊ-ቍቐ-ቖቘቚ-ቝበ-ኈኊ-ኍነ-ኰኲ-ኵኸ-ኾዀዂ-ዅወ-ዖዘ-ጐጒ-ጕጘ-ፚᎀ-ᎏᎠ-Ᏽᏸ-ᏽᐁ-ᙬᙯ-ᙿᚁ-ᚚᚠ-ᛪᛮ-ᛸᜀ-ᜌᜎ-ᜑᜠ-ᜱᝀ-ᝑᝠ-ᝬᝮ-ᝰក-ឳៗៜᠠ-ᡸᢀ-ᢨᢪᢰ-ᣵᤀ-ᤞᥐ-ᥭᥰ-ᥴᦀ-ᦫᦰ-ᧉᨀ-ᨖᨠ-ᩔᪧᬅ-ᬳᭅ-ᭋᮃ-ᮠᮮᮯᮺ-ᯥᰀ-ᰣᱍ-ᱏᱚ-ᱽᲀ-ᲈᲐ-ᲺᲽ-Ჿᳩ-ᳬᳮ-ᳳᳵᳶᳺᴀ-ᶿḀ-ἕἘ-Ἕἠ-ὅὈ-Ὅὐ-ὗὙὛὝὟ-ώᾀ-ᾴᾶ-ᾼιῂ-ῄῆ-ῌῐ-ΐῖ-Ίῠ-Ῥῲ-ῴῶ-ῼⁱⁿₐ-ₜℂℇℊ-ℓℕ℘-ℝℤΩℨK-ℹℼ-ℿⅅ-ⅉⅎⅠ-ↈⰀ-Ⱞⰰ-ⱞⱠ-ⳤⳫ-ⳮⳲⳳⴀ-ⴥⴧⴭⴰ-ⵧⵯⶀ-ⶖⶠ-ⶦⶨ-ⶮⶰ-ⶶⶸ-ⶾⷀ-ⷆⷈ-ⷎⷐ-ⷖⷘ-ⷞ々-〇〡-〩〱-〵〸-〼ぁ-ゖ゛-ゟァ-ヺー-ヿㄅ-ㄯㄱ-ㆎㆠ-ㆺㇰ-ㇿ㐀-䶵一-鿯ꀀ-ꒌꓐ-ꓽꔀ-ꘌꘐ-ꘟꘪꘫꙀ-ꙮꙿ-ꚝꚠ-ꛯꜗ-ꜟꜢ-ꞈꞋ-ꞿꟂ-Ᶎꟷ-ꠁꠃ-ꠅꠇ-ꠊꠌ-ꠢꡀ-ꡳꢂ-ꢳꣲ-ꣷꣻꣽꣾꤊ-ꤥꤰ-ꥆꥠ-ꥼꦄ-ꦲꧏꧠ-ꧤꧦ-ꧯꧺ-ꧾꨀ-ꨨꩀ-ꩂꩄ-ꩋꩠ-ꩶꩺꩾ-ꪯꪱꪵꪶꪹ-ꪽꫀꫂꫛ-ꫝꫠ-ꫪꫲ-ꫴꬁ-ꬆꬉ-ꬎꬑ-ꬖꬠ-ꬦꬨ-ꬮꬰ-ꭚꭜ-ꭧꭰ-ꯢ가-힣ힰ-ퟆퟋ-ퟻ豈-舘並-龎ﬀ-ﬆﬓ-ﬗיִײַ-ﬨשׁ-זּטּ-לּמּנּסּףּפּצּ-ﮱﯓ-ﴽﵐ-ﶏﶒ-ﷇﷰ-ﷻﹰ-ﹴﹶ-ﻼＡ-Ｚａ-ｚｦ-ﾾￂ-ￇￊ-ￏￒ-ￗￚ-ￜ𐀀-𐀋𐀍-𐀦𐀨-𐀺𐀼𐀽𐀿-𐁍𐁐-𐁝𐂀-𐃺𐅀-𐅴𐊀-𐊜𐊠-𐋐𐌀-𐌟𐌭-𐍊𐍐-𐍵𐎀-𐎝𐎠-𐏃𐏈-𐏏𐏑-𐏕𐐀-𐒝𐒰-𐓓𐓘-𐓻𐔀-𐔧𐔰-𐕣𐘀-𐜶𐝀-𐝕𐝠-𐝧𐠀-𐠅𐠈𐠊-𐠵𐠷𐠸𐠼𐠿-𐡕𐡠-𐡶𐢀-𐢞𐣠-𐣲𐣴𐣵𐤀-𐤕𐤠-𐤹𐦀-𐦷𐦾𐦿𐨀𐨐-𐨓𐨕-𐨗𐨙-𐨵𐩠-𐩼𐪀-𐪜𐫀-𐫇𐫉-𐫤𐬀-𐬵𐭀-𐭕𐭠-𐭲𐮀-𐮑𐰀-𐱈𐲀-𐲲𐳀-𐳲𐴀-𐴣𐼀-𐼜𐼧𐼰-𐽅𐿠-𐿶𑀃-𑀷𑂃-𑂯𑃐-𑃨𑄃-𑄦𑅄𑅐-𑅲𑅶𑆃-𑆲𑇁-𑇄𑇚𑇜𑈀-𑈑𑈓-𑈫𑊀-𑊆𑊈𑊊-𑊍𑊏-𑊝𑊟-𑊨𑊰-𑋞𑌅-𑌌𑌏𑌐𑌓-𑌨𑌪-𑌰𑌲𑌳𑌵-𑌹𑌽𑍐𑍝-𑍡𑐀-𑐴𑑇-𑑊𑑟𑒀-𑒯𑓄𑓅𑓇𑖀-𑖮𑗘-𑗛𑘀-𑘯𑙄𑚀-𑚪𑚸𑜀-𑜚𑠀-𑠫𑢠-𑣟𑣿𑦠-𑦧𑦪-𑧐𑧡𑧣𑨀𑨋-𑨲𑨺𑩐𑩜-𑪉𑪝𑫀-𑫸𑰀-𑰈𑰊-𑰮𑱀𑱲-𑲏𑴀-𑴆𑴈𑴉𑴋-𑴰𑵆𑵠-𑵥𑵧𑵨𑵪-𑶉𑶘𑻠-𑻲𒀀-𒎙𒐀-𒑮𒒀-𒕃𓀀-𓐮𔐀-𔙆𖠀-𖨸𖩀-𖩞𖫐-𖫭𖬀-𖬯𖭀-𖭃𖭣-𖭷𖭽-𖮏𖹀-𖹿𖼀-𖽊𖽐𖾓-𖾟𖿠𖿡𖿣𗀀-𘟷𘠀-𘫲𛀀-𛄞𛅐-𛅒𛅤-𛅧𛅰-𛋻𛰀-𛱪𛱰-𛱼𛲀-𛲈𛲐-𛲙𝐀-𝑔𝑖-𝒜𝒞𝒟𝒢𝒥𝒦𝒩-𝒬𝒮-𝒹𝒻𝒽-𝓃𝓅-𝔅𝔇-𝔊𝔍-𝔔𝔖-𝔜𝔞-𝔹𝔻-𝔾𝕀-𝕄𝕆𝕊-𝕐𝕒-𝚥𝚨-𝛀𝛂-𝛚𝛜-𝛺𝛼-𝜔𝜖-𝜴𝜶-𝝎𝝐-𝝮𝝰-𝞈𝞊-𝞨𝞪-𝟂𝟄-𝟋𞄀-𞄬𞄷-𞄽𞅎𞋀-𞋫𞠀-𞣄𞤀-𞥃𞥋𞸀-𞸃𞸅-𞸟𞸡𞸢𞸤𞸧𞸩-𞸲𞸴-𞸷𞸹𞸻𞹂𞹇𞹉𞹋𞹍-𞹏𞹑𞹒𞹔𞹗𞹙𞹛𞹝𞹟𞹡𞹢𞹤𞹧-𞹪𞹬-𞹲𞹴-𞹷𞹹-𞹼𞹾𞺀-𞺉𞺋-𞺛𞺡-𞺣𞺥-𞺩𞺫-𞺻𠀀-𪛖𪜀-𫜴𫝀-𫠝𫠠-𬺡𬺰-𮯠丽-𪘀]""".r
  lazy val IDContinue: Parser[String] = """[0-9A-Z_a-zªµ·ºÀ-ÖØ-öø-ˁˆ-ˑˠ-ˤˬˮ̀-ʹͶͷͺ-ͽͿΆ-ΊΌΎ-ΡΣ-ϵϷ-ҁ҃-҇Ҋ-ԯԱ-Ֆՙՠ-ֈ֑-ׇֽֿׁׂׅׄא-תׯ-ײؐ-ؚؠ-٩ٮ-ۓە-ۜ۟-۪ۨ-ۼۿܐ-݊ݍ-ޱ߀-ߵߺ߽ࠀ-࠭ࡀ-࡛ࡠ-ࡪࢠ-ࢴࢶ-ࢽ࣓-ࣣ࣡-ॣ०-९ॱ-ঃঅ-ঌএঐও-নপ-রলশ-হ়-ৄেৈো-ৎৗড়ঢ়য়-ৣ০-ৱৼ৾ਁ-ਃਅ-ਊਏਐਓ-ਨਪ-ਰਲਲ਼ਵਸ਼ਸਹ਼ਾ-ੂੇੈੋ-੍ੑਖ਼-ੜਫ਼੦-ੵઁ-ઃઅ-ઍએ-ઑઓ-નપ-રલળવ-હ઼-ૅે-ૉો-્ૐૠ-ૣ૦-૯ૹ-૿ଁ-ଃଅ-ଌଏଐଓ-ନପ-ରଲଳଵ-ହ଼-ୄେୈୋ-୍ୖୗଡ଼ଢ଼ୟ-ୣ୦-୯ୱஂஃஅ-ஊஎ-ஐஒ-கஙசஜஞடணதந-பம-ஹா-ூெ-ைொ-்ௐௗ௦-௯ఀ-ఌఎ-ఐఒ-నప-హఽ-ౄె-ైొ-్ౕౖౘ-ౚౠ-ౣ౦-౯ಀ-ಃಅ-ಌಎ-ಐಒ-ನಪ-ಳವ-ಹ಼-ೄೆ-ೈೊ-್ೕೖೞೠ-ೣ೦-೯ೱೲഀ-ഃഅ-ഌഎ-ഐഒ-ൄെ-ൈൊ-ൎൔ-ൗൟ-ൣ൦-൯ൺ-ൿංඃඅ-ඖක-නඳ-රලව-ෆ්ා-ුූෘ-ෟ෦-෯ෲෳก-ฺเ-๎๐-๙ກຂຄຆ-ຊຌ-ຣລວ-ຽເ-ໄໆ່-ໍ໐-໙ໜ-ໟༀ༘༙༠-༩༹༵༷༾-ཇཉ-ཬཱ-྄྆-ྗྙ-ྼ࿆က-၉ၐ-ႝႠ-ჅჇჍა-ჺჼ-ቈቊ-ቍቐ-ቖቘቚ-ቝበ-ኈኊ-ኍነ-ኰኲ-ኵኸ-ኾዀዂ-ዅወ-ዖዘ-ጐጒ-ጕጘ-ፚ፝-፟፩-፱ᎀ-ᎏᎠ-Ᏽᏸ-ᏽᐁ-ᙬᙯ-ᙿᚁ-ᚚᚠ-ᛪᛮ-ᛸᜀ-ᜌᜎ-᜔ᜠ-᜴ᝀ-ᝓᝠ-ᝬᝮ-ᝰᝲᝳក-៓ៗៜ៝០-៩-᠐-᠙ᠠ-ᡸᢀ-ᢪᢰ-ᣵᤀ-ᤞᤠ-ᤫᤰ-᤻᥆-ᥭᥰ-ᥴᦀ-ᦫᦰ-ᧉ᧐-᧚ᨀ-ᨛᨠ-ᩞ᩠-᩿᩼-᪉᪐-᪙ᪧ᪰-᪽ᬀ-ᭋ᭐-᭙᭫-᭳ᮀ-᯳ᰀ-᰷᱀-᱉ᱍ-ᱽᲀ-ᲈᲐ-ᲺᲽ-Ჿ᳐-᳔᳒-ᳺᴀ-᷹᷻-ἕἘ-Ἕἠ-ὅὈ-Ὅὐ-ὗὙὛὝὟ-ώᾀ-ᾴᾶ-ᾼιῂ-ῄῆ-ῌῐ-ΐῖ-Ίῠ-Ῥῲ-ῴῶ-ῼ‿⁀⁔ⁱⁿₐ-ₜ⃐-⃥⃜⃡-⃰ℂℇℊ-ℓℕ℘-ℝℤΩℨK-ℹℼ-ℿⅅ-ⅉⅎⅠ-ↈⰀ-Ⱞⰰ-ⱞⱠ-ⳤⳫ-ⳳⴀ-ⴥⴧⴭⴰ-ⵧⵯ⵿-ⶖⶠ-ⶦⶨ-ⶮⶰ-ⶶⶸ-ⶾⷀ-ⷆⷈ-ⷎⷐ-ⷖⷘ-ⷞⷠ-ⷿ々-〇〡-〯〱-〵〸-〼ぁ-ゖ゙-ゟァ-ヺー-ヿㄅ-ㄯㄱ-ㆎㆠ-ㆺㇰ-ㇿ㐀-䶵一-鿯ꀀ-ꒌꓐ-ꓽꔀ-ꘌꘐ-ꘫꙀ-꙯ꙴ-꙽ꙿ-꛱ꜗ-ꜟꜢ-ꞈꞋ-ꞿꟂ-Ᶎꟷ-ꠧꡀ-ꡳꢀ-ꣅ꣐-꣙꣠-ꣷꣻꣽ-꤭ꤰ-꥓ꥠ-ꥼꦀ-꧀ꧏ-꧙ꧠ-ꧾꨀ-ꨶꩀ-ꩍ꩐-꩙ꩠ-ꩶꩺ-ꫂꫛ-ꫝꫠ-ꫯꫲ-꫶ꬁ-ꬆꬉ-ꬎꬑ-ꬖꬠ-ꬦꬨ-ꬮꬰ-ꭚꭜ-ꭧꭰ-ꯪ꯬꯭꯰-꯹가-힣ힰ-ퟆퟋ-ퟻ豈-舘並-龎ﬀ-ﬆﬓ-ﬗיִ-ﬨשׁ-זּטּ-לּמּנּסּףּפּצּ-ﮱﯓ-ﴽﵐ-ﶏﶒ-ﷇﷰ-ﷻ-︠-︯︳︴﹍-﹏ﹰ-ﹴﹶ-ﻼ０-９Ａ-Ｚ＿ａ-ｚｦ-ﾾￂ-ￇￊ-ￏￒ-ￗￚ-ￜ𐀀-𐀋𐀍-𐀦𐀨-𐀺𐀼𐀽𐀿-𐁍𐁐-𐁝𐂀-𐃺𐅀-𐅴𐇽𐊀-𐊜𐊠-𐋐𐋠𐌀-𐌟𐌭-𐍊𐍐-𐍺𐎀-𐎝𐎠-𐏃𐏈-𐏏𐏑-𐏕𐐀-𐒝𐒠-𐒩𐒰-𐓓𐓘-𐓻𐔀-𐔧𐔰-𐕣𐘀-𐜶𐝀-𐝕𐝠-𐝧𐠀-𐠅𐠈𐠊-𐠵𐠷𐠸𐠼𐠿-𐡕𐡠-𐡶𐢀-𐢞𐣠-𐣲𐣴𐣵𐤀-𐤕𐤠-𐤹𐦀-𐦷𐦾𐦿𐨀-𐨃𐨅𐨆𐨌-𐨓𐨕-𐨗𐨙-𐨵𐨸-𐨿𐨺𐩠-𐩼𐪀-𐪜𐫀-𐫇𐫉-𐫦𐬀-𐬵𐭀-𐭕𐭠-𐭲𐮀-𐮑𐰀-𐱈𐲀-𐲲𐳀-𐳲𐴀-𐴧𐴰-𐴹𐼀-𐼜𐼧𐼰-𐽐𐿠-𐿶𑀀-𑁆𑁦-𑁯𑁿-𑂺𑃐-𑃨𑃰-𑃹𑄀-𑄴𑄶-𑄿𑅄-𑅆𑅐-𑅳𑅶𑆀-𑇄𑇉-𑇌𑇐-𑇚𑇜𑈀-𑈑𑈓-𑈷𑈾𑊀-𑊆𑊈𑊊-𑊍𑊏-𑊝𑊟-𑊨𑊰-𑋪𑋰-𑋹𑌀-𑌃𑌅-𑌌𑌏𑌐𑌓-𑌨𑌪-𑌰𑌲𑌳𑌵-𑌹𑌻-𑍄𑍇𑍈𑍋-𑍍𑍐𑍗𑍝-𑍣𑍦-𑍬𑍰-𑍴𑐀-𑑊𑑐-𑑙𑑞𑑟𑒀-𑓅𑓇𑓐-𑓙𑖀-𑖵𑖸-𑗀𑗘-𑗝𑘀-𑙀𑙄𑙐-𑙙𑚀-𑚸𑛀-𑛉𑜀-𑜚𑜝-𑜫𑜰-𑜹𑠀-𑠺𑢠-𑣩𑣿𑦠-𑦧𑦪-𑧗𑧚-𑧡𑧣𑧤𑨀-𑨾𑩇𑩐-𑪙𑪝𑫀-𑫸𑰀-𑰈𑰊-𑰶𑰸-𑱀𑱐-𑱙𑱲-𑲏𑲒-𑲧𑲩-𑲶𑴀-𑴆𑴈𑴉𑴋-𑴶𑴺𑴼𑴽𑴿-𑵇𑵐-𑵙𑵠-𑵥𑵧𑵨𑵪-𑶎𑶐𑶑𑶓-𑶘𑶠-𑶩𑻠-𑻶𒀀-𒎙𒐀-𒑮𒒀-𒕃𓀀-𓐮𔐀-𔙆𖠀-𖨸𖩀-𖩞𖩠-𖩩𖫐-𖫭𖫰-𖫴𖬀-𖬶𖭀-𖭃𖭐-𖭙𖭣-𖭷𖭽-𖮏𖹀-𖹿𖼀-𖽊𖽏-𖾇𖾏-𖾟𖿠𖿡𖿣𗀀-𘟷𘠀-𘫲𛀀-𛄞𛅐-𛅒𛅤-𛅧𛅰-𛋻𛰀-𛱪𛱰-𛱼𛲀-𛲈𛲐-𛲙𛲝𛲞𝅥-𝅩𝅭-𝅲𝅻-𝆂𝆅-𝆋𝆪-𝆭𝉂-𝉄𝐀-𝑔𝑖-𝒜𝒞𝒟𝒢𝒥𝒦𝒩-𝒬𝒮-𝒹𝒻𝒽-𝓃𝓅-𝔅𝔇-𝔊𝔍-𝔔𝔖-𝔜𝔞-𝔹𝔻-𝔾𝕀-𝕄𝕆𝕊-𝕐𝕒-𝚥𝚨-𝛀𝛂-𝛚𝛜-𝛺𝛼-𝜔𝜖-𝜴𝜶-𝝎𝝐-𝝮𝝰-𝞈𝞊-𝞨𝞪-𝟂𝟄-𝟋𝟎-𝟿𝨀-𝨶𝨻-𝩬𝩵𝪄𝪛-𝪟𝪡-𝪯𞀀-𞀆𞀈-𞀘𞀛-𞀡𞀣𞀤𞀦-𞀪𞄀-𞄬𞄰-𞄽𞅀-𞅉𞅎𞋀-𞋹𞠀-𞣄𞣐-𞣖𞤀-𞥋𞥐-𞥙𞸀-𞸃𞸅-𞸟𞸡𞸢𞸤𞸧𞸩-𞸲𞸴-𞸷𞸹𞸻𞹂𞹇𞹉𞹋𞹍-𞹏𞹑𞹒𞹔𞹗𞹙𞹛𞹝𞹟𞹡𞹢𞹤𞹧-𞹪𞹬-𞹲𞹴-𞹷𞹹-𞹼𞹾𞺀-𞺉𞺋-𞺛𞺡-𞺣𞺥-𞺩𞺫-𞺻𠀀-𪛖𪜀-𫜴𫝀-𫠝𫠠-𬺡𬺰-𮯠丽-𪘀-]""".r

  lazy val Comment: Parser[String] = """/\*+[^*]*\*+(?:[^/*][^*]*\*+)*/|//[^\u000A\u000D\u2028\u2029]*""".r

  // sequence
  def seq(p1: => Parser[String]): Parser[String] = p1
  def seq(p1: => Parser[String], p2: => Parser[String]): Parser[String] =
    p1 ~ p2 ^^ { case x1 ~ x2 => x1 + x2 }
  def seq(p1: => Parser[String], p2: => Parser[String], p3: => Parser[String]): Parser[String] =
    p1 ~ p2 ~ p3 ^^ { case x1 ~ x2 ~ x3 => x1 + x2 + x3 }
  def seq(p1: => Parser[String], p2: => Parser[String], p3: => Parser[String], p4: => Parser[String]): Parser[String] =
    p1 ~ p2 ~ p3 ~ p4 ^^ { case x1 ~ x2 ~ x3 ~ x4 => x1 + x2 + x3 + x4 }
  def seq(p1: => Parser[String], p2: => Parser[String], p3: => Parser[String], p4: => Parser[String], p5: => Parser[String]): Parser[String] =
    p1 ~ p2 ~ p3 ~ p4 ~ p5 ^^ { case x1 ~ x2 ~ x3 ~ x4 ~ x5 => x1 + x2 + x3 + x4 + x5 }
  def seq(p1: => Parser[String], p2: => Parser[String], p3: => Parser[String], p4: => Parser[String], p5: => Parser[String], p6: => Parser[String]): Parser[String] =
    p1 ~ p2 ~ p3 ~ p4 ~ p5 ~ p6 ^^ { case x1 ~ x2 ~ x3 ~ x4 ~ x5 ~ x6 => x1 + x2 + x3 + x4 + x5 + x6 }

  def strOpt(parser: => Parser[String]): Parser[String] = parser | STR_MATCH

  lazy val Skip: Parser[String] = ((WhiteSpace | LineTerminator | Comment)*) ^^ { _.mkString }
  lazy val NoLineTerminator: NodeParser[String] = NodeParser(first => strNoLineTerminator, emptyFirst)
  lazy val strNoLineTerminator: Parser[String] = STR_MATCH <~ +(Skip.filter(s => lines.findFirstIn(s).isEmpty))
  def term(name: String, nt: Parser[String]): NodeParser[String] = NodeParser(first => Skip ~> nt <~ Skip <~ +first.getParser, FirstTerms() + (name -> nt))
  def term(t: String): NodeParser[String] = NodeParser(first => { Skip ~> t <~ Skip <~ +first.getParser }, FirstTerms() + t)

  lazy val emptyFirst: FirstTerms = FirstTerms(ts = Set(""))
  case class FirstTerms(ts: Set[String] = Set(), nts: Map[String, Parser[String]] = Map()) {
    def +(that: FirstTerms): FirstTerms = FirstTerms(this.ts ++ that.ts, this.nts ++ that.nts)
    def +(t: String): FirstTerms = copy(ts = ts + t)
    def +(nt: (String, Parser[String])): FirstTerms = copy(nts = nts + nt)
    def ~(that: => FirstTerms): FirstTerms =
      if (this.ts contains "") FirstTerms(this.ts - "" ++ that.ts, this.nts ++ that.nts)
      else this
    def getParser: Parser[String] = (((STR_MISMATCH: Parser[String]) /: ts)(_ | _) /: nts)(_ | _._2)
    override def toString: String = (ts ++ nts.map(_._1)).map("\"" + _ + "\"").mkString("[", ", ", "]")
  }

  lazy val MATCH: NodeParser[String] = NodeParser(first => "" <~ +first.getParser, FirstTerms() + "")
  lazy val MISMATCH: NodeParser[Nothing] = NodeParser(first => failure(""), FirstTerms())

  case class NodeParser[+T](parser: FirstTerms => Parser[T], first: FirstTerms) {
    def ~[U](that: => NodeParser[U]): NodeParser[~[T, U]] =
      NodeParser(first => this.parser(that.first ~ first) ~ that.parser(first), this.first ~ that.first)

    def ~>[U](that: => NodeParser[U]): NodeParser[U] =
      NodeParser(first => this.parser(that.first ~ first) ~> that.parser(first), this.first ~ that.first)

    def <~[U](that: => NodeParser[U]): NodeParser[T] =
      NodeParser(first => this.parser(that.first ~ first) <~ that.parser(first), this.first ~ that.first)

    def |[U >: T](that: NodeParser[U]): NodeParser[U] =
      if (that eq MISMATCH) this
      else NodeParser(first => this.parser(first) | that.parser(first), this.first + that.first)

    def ^^[U](f: T => U): NodeParser[U] =
      NodeParser(first => this.parser(first) ^^ f, this.first)

    def ^^^[U](v: => U): NodeParser[U] =
      NodeParser(first => this.parser(first) ^^^ v, this.first)

    def apply(first: FirstTerms, in: Reader[Char]): ParseResult[T] = parser(first)(in)

    def unary_-(): NodeParser[Unit] =
      NodeParser(first => -parser(first), emptyFirst)

    def unary_+(): NodeParser[Unit] =
      NodeParser(first => +parser(first), emptyFirst)
  }

  def phrase[T](p: => NodeParser[T]): NodeParser[T] =
    NodeParser(first => phrase(p.parser(first)), p.first)

  def opt[T](p: => NodeParser[T]): NodeParser[Option[T]] =
    NodeParser(first => opt(p.parser(first)), p.first + "")

  /** Parse some prefix of reader `in` with parser `p`. */
  def parse[T](p: NodeParser[T], in: Reader[Char]): ParseResult[T] =
    p(emptyFirst, in)

  /** Parse some prefix of character sequence `in` with parser `p`. */
  def parse[T](p: NodeParser[T], in: java.lang.CharSequence): ParseResult[T] =
    parse(p, new CharSequenceReader(in))

  /** Parse some prefix of reader `in` with parser `p`. */
  def parse[T](p: NodeParser[T], in: java.io.Reader): ParseResult[T] =
    parse(p, new PagedSeqReader(PagedSeq.fromReader(in)))

  /** Parse all of reader `in` with parser `p`. */
  def parseAll[T](p: NodeParser[T], in: Reader[Char]): ParseResult[T] =
    parse(phrase(p), in)

  /** Parse all of reader `in` with parser `p`. */
  def parseAll[T](p: NodeParser[T], in: java.io.Reader): ParseResult[T] =
    parse(phrase(p), in)

  /** Parse all of character sequence `in` with parser `p`. */
  def parseAll[T](p: NodeParser[T], in: java.lang.CharSequence): ParseResult[T] =
    parse(phrase(p), in)

  var keepLog: Boolean = true
  def log[T](p: NodeParser[T])(name: String): NodeParser[T] = NodeParser(first => Parser { in =>
    val stopMsg = s"trying $name with $first at [${in.pos}] \n\n${in.pos.longString}\n"
    if (keepLog) stop(stopMsg) match {
      case "q" =>
        keepLog = false
        p(first, in)
      case "j" =>
        keepLog = false
        val r = p(first, in)
        println(name + " --> " + r)
        keepLog = true
        r
      case _ =>
        val r = p(first, in)
        println(name + " --> " + r)
        r
    }
    else p(first, in)
  }, p.first)

  private def stop(msg: String): String = {
    println(msg)
    scala.io.StdIn.readLine
  }

  type P0[T] = NodeParser[T]
  type P1[T] = (Boolean) => NodeParser[T]
  type P2[T] = ((Boolean, Boolean)) => NodeParser[T]
  type P3[T] = ((Boolean, Boolean, Boolean)) => NodeParser[T]
  type R0[T] = NodeParser[T => T]
  type R1[T] = (Boolean) => NodeParser[T => T]
  type R2[T] = ((Boolean, Boolean)) => NodeParser[T => T]
  type R3[T] = ((Boolean, Boolean, Boolean)) => NodeParser[T => T]
  protected def memo[K, V](f: K => V): K => V = {
    val cache = collection.mutable.Map.empty[K, V]
    k => cache.getOrElse(k, {
      val v = f(k)
      cache.update(k, v)
      v
    })
  }

  val Script: P0[Script]

  def apply(filename: String): Script =
    parseAll(term("") ~> Script, fileReader(filename)).get
}
