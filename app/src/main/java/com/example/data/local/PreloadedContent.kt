package com.example.data.local

import com.example.data.model.ContentDayPlan
import com.example.data.model.HookCategory
import com.example.data.model.HookItem
import com.example.data.model.ScriptTemplate

object PreloadedContent {

    val hooks: List<HookItem> = listOf(
        // ONLINE_SHOP
        HookItem(
            id = "shop_1",
            title = "افشای قیمت واقعی",
            category = HookCategory.ONLINE_SHOP,
            hookFa = "این محصول رو به هیچ وجه نخر، مگر اینکه بدونی واقعاً چقدر واسه ما تموم شده!",
            psychologyExplanation = "کنجکاوی شدید و حس شوک؛ کاربر احساس می‌کند فروشنده قرار است رازی را فاش کند.",
            visualSceneTip = "دوربین نزدیک به محصول، با دست محصول را هل دادن به عقب و نگاه مستقیم به لنز.",
            isVipOnly = false,
            estimatedViewPotential = "+650K"
        ),
        HookItem(
            id = "shop_2",
            title = "اشتباه در ارسال سفارش",
            category = HookCategory.ONLINE_SHOP,
            hookFa = "بزرگترین سوتی هفته‌مون رو دادیم و این بسته الان دست مشتریه...",
            psychologyExplanation = "قصه‌گویی اعترافی (Confessional Storytelling) که باعث جلب اعتماد و حس صمیمیت می‌شود.",
            visualSceneTip = "در حال بسته‌بندی در شاپ با چسب پهن و استیکر پست، دوربین با زاویه بالا.",
            isVipOnly = false,
            estimatedViewPotential = "+480K"
        ),
        HookItem(
            id = "shop_3",
            title = "مقایسه فیک و اورجینال",
            category = HookCategory.ONLINE_SHOP,
            hookFa = "۹۰ درصدتون دارین این مدل فیک رو به اسم اصل می‌خرین، بیا بهت بگم فرقش چیه!",
            psychologyExplanation = "ترس از ضرر مالی (FOMO & Loss Aversion) مخاطب را مجبور به سیو و تماشای کامل می‌کند.",
            visualSceneTip = "دو محصول در کنار هم، اشاره انگشت به جزئیات ریز و زوم روی بافت.",
            isVipOnly = true,
            estimatedViewPotential = "+920K"
        ),
        HookItem(
            id = "shop_4",
            title = "کارت تخفیف مخفی",
            category = HookCategory.ONLINE_SHOP,
            hookFa = "فقط ۳ نفر اولی که این ۳ کلمه رو تو کامنت بنویسن، این محصول رو رایگان می‌برن!",
            psychologyExplanation = "انگیزه‌ی گیمیفیکیشن و کامنت‌گذاری انفجاری که الگوریتم اینستاگرام را فعال می‌کند.",
            visualSceneTip = "نگه‌داشتن محصول در دست و تکان دادن سریع با زیرنویس بولد متحرک.",
            isVipOnly = true,
            estimatedViewPotential = "+800K"
        ),

        // EDUCATION
        HookItem(
            id = "edu_1",
            title = "اشتباه رایج که پیجت رو نابود می‌کنه",
            category = HookCategory.EDUCATION,
            hookFa = "اگر هنوز این کار رو انجام میدی، با دستای خودت داری پیجت رو شادوبن می‌کنی!",
            psychologyExplanation = "اضطراب از بین رفتن زحمات و کنجکاوی برای چک کردن تنظیمات پیج.",
            visualSceneTip = "صفحه گوشی با اسکرین‌شات قرمز شده از افت بازدید، انگشت اشاره رو به دوربین.",
            isVipOnly = false,
            estimatedViewPotential = "+540K"
        ),
        HookItem(
            id = "edu_2",
            title = "ترفند ۳ ثانیه‌ای کمتر شناخته‌شده",
            category = HookCategory.EDUCATION,
            hookFa = "این ترفند ۳ ثانیه‌ای رو هیچ استادی بهت رایگان نمیگه چون بازار کارش کساد میشه!",
            psychologyExplanation = "شکستن انحصار اطلاعاتی؛ کاربر احساس کشف یک گنج پنهان را دارد.",
            visualSceneTip = "تایپ کردن سریع روی کیبورد یا لپ‌تاپ و بعد چرخاندن صندلی به سمت دوربین.",
            isVipOnly = false,
            estimatedViewPotential = "+710K"
        ),
        HookItem(
            id = "edu_3",
            title = "قانون ۱۰ به ۹۰",
            category = HookCategory.EDUCATION,
            hookFa = "۱۰ درصد ادمین‌ها فقط به این نکته عمل می‌کنن و ۹۰ درصد بقیه تو فقر بازدید موندن!",
            psychologyExplanation = "تمایل ذاتی انسان به تعلق داشتن به گروه نخبه و برنده.",
            visualSceneTip = "کشیدن دو دایره روی تخته سفید یا کاغذ و علامت زدن بخش ۱۰ درصدی.",
            isVipOnly = true,
            estimatedViewPotential = "+1.1M"
        ),

        // LIFESTYLE
        HookItem(
            id = "life_1",
            title = "تصمیمی که زندگیمو تغییر داد",
            category = HookCategory.LIFESTYLE,
            hookFa = "دقیقاً پارسال همین موقع هیچ امیدی نداشتم، تا اینکه این یک عادت رو شروع کردم...",
            psychologyExplanation = "همدلی حسی، تضاد وضعیت قبل و بعد (Transformation Arc).",
            visualSceneTip = "نمای لانگ‌شات از پنجره یا قهوه ریختن با موسیقی ملایم و اتمسفریک.",
            isVipOnly = false,
            estimatedViewPotential = "+390K"
        ),
        HookItem(
            id = "life_2",
            title = "حرفی که هیچ‌کس بهت نمیزنه",
            category = HookCategory.LIFESTYLE,
            hookFa = "متأسفم ولی تو تنبل نیستی، مغزت داره از این تله برای فرار از کار استفاده می‌کنه!",
            psychologyExplanation = "اعتبارسنجی احساس گناه مخاطب و دادن راه‌حل علمی و منطقی.",
            visualSceneTip = "قدم زدن در پارک یا خیابان با دوربین سلفی در حال حرکت پویا.",
            isVipOnly = false,
            estimatedViewPotential = "+620K"
        ),
        HookItem(
            id = "life_3",
            title = "روتین صبحگاهی مخفی ثروتمندان",
            category = HookCategory.LIFESTYLE,
            hookFa = "این ۳۰ دقیقه از روزت رو اگر اینطوری بگذرونی، تمرکزت ۵ برابر میشه!",
            psychologyExplanation = "اشتیاق به بهینه‌سازی عملکرد فردی و خودارتقایی سریع.",
            visualSceneTip = "تایم‌لپس طلوع آفتاب و بستن دفتر برنامه‌ریزی روزانه.",
            isVipOnly = true,
            estimatedViewPotential = "+850K"
        ),

        // CONTROVERSIAL
        HookItem(
            id = "cont_1",
            title = "دروغ بزرگی که به خوردتون دادن",
            category = HookCategory.CONTROVERSIAL,
            hookFa = "همه دارن بهت میگن ریلز روزی ۳ تا بذار؛ ولی این بزرگترین دروغ اینستاگرامه!",
            psychologyExplanation = "مخالفت صریح با باورهای رایج (Contrarian Statement) که باعث توقف اسکرول می‌شود.",
            visualSceneTip = "تکان دادن دست به نشانه نفی قاطع، با صدای افکت صوتی بیپ یا ضربه.",
            isVipOnly = false,
            estimatedViewPotential = "+880K"
        ),
        HookItem(
            id = "cont_2",
            title = "پاک کردن این برنامه فوراً",
            category = HookCategory.CONTROVERSIAL,
            hookFa = "اگر این برنامه هنوز روی گوشیت نصبه، همین الان پاکش کن چون باتری و رم رو می‌بلعه!",
            psychologyExplanation = "حس اورژانسی و خطر فوری که وادار به اقدام بی‌درنگ می‌کند.",
            visualSceneTip = "نگه‌داشتن آیکون یک اپ معروف روی صفحه نمایش و بردن روی سطل زباله.",
            isVipOnly = false,
            estimatedViewPotential = "+950K"
        ),
        HookItem(
            id = "cont_3",
            title = "راز پنهان پیج‌های میلیونی",
            category = HookCategory.CONTROVERSIAL,
            hookFa = "پیج‌های میلیونی هرگز نمیخوان بدونی که چطور بدون تبلیغات فقط با این باگ بالا اومدن!",
            psychologyExplanation = "تئوری توطئه مثبت و حس دسترسی به میانبر ناعادلانه.",
            visualSceneTip = "زمزمه کردن رو به دوربین و نزدیک شدن لنز به صورت، گویی رازی در میان است.",
            isVipOnly = true,
            estimatedViewPotential = "+1.4M"
        ),

        // TECH_AI
        HookItem(
            id = "tech_1",
            title = "سایتی که رایگان کار ۳ نفرو می‌کنه",
            category = HookCategory.TECH_AI,
            hookFa = "به جای اینکه ماهی ۱۰ میلیون حقوق طراح بدی، این هوش مصنوعی رایگان کارت رو راه میندازه!",
            psychologyExplanation = "صرفه‌جویی ملموس مالی و افزایش بهره‌وری کسب‌وکار.",
            visualSceneTip = "فیلم گرفتن از مانیتور در حال تولید خودکار بنر با پرامپت ساده.",
            isVipOnly = false,
            estimatedViewPotential = "+760K"
        ),
        HookItem(
            id = "tech_2",
            title = "دستور پنهانی ChatGPT",
            category = HookCategory.TECH_AI,
            hookFa = "این پرامپت طلایی رو به هوش مصنوعی بده تا درجا متن ۳۰ تا ریلز وایرال برات بنویسه!",
            psychologyExplanation = "راه‌حل حاضر و آماده بدون نیاز به فکر کردن و صرف وقت.",
            visualSceneTip = "پیست کردن متن در چت هوش مصنوعی و اسکرول سریع خروجی‌های شگفت‌انگیز.",
            isVipOnly = true,
            estimatedViewPotential = "+1.2M"
        )
    )

    val templates: List<ScriptTemplate> = listOf(
        ScriptTemplate(
            id = "temp_1",
            title = "سناریوی انفجار فروش محصول (روش تضاد قیمت)",
            category = HookCategory.ONLINE_SHOP,
            durationSeconds = 45,
            hookTemplate = "این [محصول یا سرویس] رو هرگز نخر، مگر اینکه بدونی چطوری قیمت واقعیش یک سوم بازار میشه!",
            bodyTemplate = "اکثر فروشنده‌ها این نکته رو پنهان می‌کنن، اما راز اصلی در [علت تفاوت کیفیت یا تامین مستقیم] هست. وقتی شما از [ویژگی کلیدی یا گارانتی محصول] استفاده می‌کنی، در واقع داری از [بزرگترین ضرر مشتری] جلوگیری می‌کنی و این ۳ مزیت رو می‌گیری:\n۱. دوام فوق‌العاده\n۲. ارسال فوری رایگان\n۳. تست ۷ روزه بدون قید و شرط.",
            ctaTemplate = "برای دریافت کد تخفیف اختصاصی ۳۰ درصدی که فقط تا امشب فعاله، کلمه «سفارش» رو همین الان تو دایرکت بفرست!",
            isVipOnly = false,
            placeholderTopic = "کفش چرم طبیعی",
            placeholderBenefit = "دوام ۱۰ ساله و قیمت نصف بازار",
            placeholderObstacle = "پاره شدن زودهنگام کفش‌های متفرقه"
        ),
        ScriptTemplate(
            id = "temp_2",
            title = "سناریوی حل اشتباه مرگبار پیج (روش زنگ خطر)",
            category = HookCategory.EDUCATION,
            durationSeconds = 50,
            hookTemplate = "اگر توی هر پست داری این اشتباه رو تکرار می‌کنی، بدون که الگوریتم رسماً پیجت رو منجمد کرده!",
            bodyTemplate = "اشتباه اینه که [اشتباه رایج مثل هشتگ نامربوط یا شروع بدون قلاب]. وقتی این کار رو می‌کنی، مخاطب در ۲ ثانیه اول اسکرول می‌کنه و نرخ واچ‌تایم افت می‌کنه.\nراه‌حل درست چیه؟ فقط کافیه از این فرمول ۳ مرحله‌ای استفاده کنی:\nاول: شوک در ۳ ثانیه اول با متن بولد.\nدوم: دادن اصل مطلب بدون مقدمه چینی.\nسوم: سوال چالشی در انتها برای ثبت کامنت.",
            ctaTemplate = "این ویدیو رو برای دوستت که آنلاین‌شاپ داره بفرست و این پست رو سیو کن تا دفعه بعد گمش نکنی!",
            isVipOnly = false,
            placeholderTopic = "روش صحیح کپشن‌نویسی",
            placeholderBenefit = "افزایش تعامل و رفتن به اکسپلور",
            placeholderObstacle = "نوشتن کپشن‌های طولانی و خسته‌کننده"
        ),
        ScriptTemplate(
            id = "temp_3",
            title = "سناریوی ۳ راز مگوی حرفه‌ای‌ها (VIP)",
            category = HookCategory.TECH_AI,
            durationSeconds = 60,
            hookTemplate = "این ۳ ابزار رایگان رو هیچ متخصصی لو نمیده، چون باهاشون پروژه‌های دلاری می‌بنده!",
            bodyTemplate = "ابزار اول: [نام ابزار ۱] برای ادیت خودکار زیرنویس فارسی با انیمیشن‌های حرفه‌ای.\nابزار دوم: [نام ابزار ۲] برای حذف نویز صدا و استودیویی کردن صدای میکروفون گوشی بدون خرید میکروفون بویا.\nابزار سوم که از همه جادویی‌تره: [نام ابزار ۳] که با یک کلیک قلاب‌های میلیونی متناسب با حوزه کاریت رو می‌سازه.",
            ctaTemplate = "لینک دسترسی مستقیم به هر ۳ ابزار رو توی استوری گذاشتم، همین الان هایلایت «ابزارها» رو چک کن!",
            isVipOnly = true,
            placeholderTopic = "ابزارهای رایگان ادیت و هوش مصنوعی",
            placeholderBenefit = "تولید ویدیوی میلیونی بدون سیستم قوی",
            placeholderObstacle = "هزینه‌های میلیونی برای خرید تجهیزات"
        ),
        ScriptTemplate(
            id = "temp_4",
            title = "سناریوی تغییر نگرش و تحول شخصی (Storytelling)",
            category = HookCategory.LIFESTYLE,
            durationSeconds = 40,
            hookTemplate = "می‌دونستی تنها فرقی که بین تو و کسی که به اهدافش رسیده وجود داره فقط ۱۵ دقیقه در روزه؟",
            bodyTemplate = "منم تا ۶ ماه پیش فکر می‌کردم نیاز به ساعات کار طولانی دارم. اما وقتی قانون اتمی خرد رو پیاده کردم، فهمیدم اگر فقط هر روز ۱۵ دقیقه روی [مهارت اصلی] تمرکز عمیق بدون گوشی بذارم، بعد از ۹۰ روز نتیجه شگفت‌انگیز میشه. نتایجش برای من چی بود؟ [نتیجه ملموس].",
            ctaTemplate = "اگر تو هم از امروز آماده‌ای این تعهد رو به خودت بدی، کلمه «شروع» رو کامنت کن تا رفیق این مسیر باشیم!",
            isVipOnly = false,
            placeholderTopic = "تمرکز روزانه بر مهارت جدید",
            placeholderBenefit = "رسیدن به استقلال و درآمد پایدار",
            placeholderObstacle = "تعلل و غرق شدن در شبکه‌های اجتماعی"
        )
    )

    val plannerDays: List<ContentDayPlan> = (1..30).map { day ->
        when (day) {
            1 -> ContentDayPlan(1, "معرفی جنجالی با قلاب معکوس", "آشنایی", "چرا نباید از پیج من خرید کنی؟!", "فیلمبرداری در فضای کاری با لبخند صمیمی و لحن خودمانی.")
            2 -> ContentDayPlan(2, "افشای پشت صحنه و بسته‌بندی", "اعتمادسازی", "بزرگترین سوتی ارسال بسته این ماه!", "تایم‌لپس پرانرژی از کارگاه یا میز کار با ترنزیشن سریع.")
            3 -> ContentDayPlan(3, "پاسخ به سوال پرتکرار دایرکت", "آموزشی", "این سوال رو روزی ۲۰ بار از من می‌پرسین...", "گرفتن اسکرین‌شات دایرکت با بلور کردن آیدی و توضیح رو به دوربین.")
            4 -> ContentDayPlan(4, "مقایسه قبل و بعد با افکت صدا", "ارزش‌آفرینی", "قبل از اینکه این ترفند رو بدونی vs بعدش!", "تقسیم صفحه به دو بخش یا سوییچ تصویر با بشکن زدن.")
            5 -> ContentDayPlan(5, "نقد یک باور غلط در صنف خودت", "جنجالی", "هر کی بهت گفته این روش جواب میده بهت دروغ گفته!", "صحبت جدی و قاطع، موسیقی با ریتم هیجانی.")
            6 -> ContentDayPlan(6, "معرفی ارزان‌ترین محصول با ارزش بالا", "فروش", "با پول یک پیتزا این تحول رو ایجاد کن!", "نمایش زوایای مختلف محصول در دست با نور طبیعی پنجره.")
            7 -> ContentDayPlan(7, "روز استراحت و تعامل در استوری", "تعامل", "چالش باکس سوالات مخاطبان در استوری", "اشتراک نظرها و هایلایت برگزیده‌ها.")
            8 -> ContentDayPlan(8, "معرفی ابزار مخفی کارت", "آموزشی", "این ابزار مثل دستیار رایگان برام کار می‌کنه!", "فیلمبرداری از صفحه مانیتور یا اسکرین‌رکورد گوشی.")
            9 -> ContentDayPlan(9, "داستان شکست و درس بزرگ", "برندینگ", "چطور تو یک روز ۱۰ میلیون ضرر دادم؟", "روایتگری آرام در فضای کافه یا حیاط.")
            10 -> ContentDayPlan(10, "رضایت مشتری با مستندات شگفت‌انگیز", "اثبات اجتماعی", "پیام صوتی مشتری بعد از یک هفته استفاده!", "پخش وویس مشتری با زیرنویس جذاب و نمایش محصول.")
            11 -> ContentDayPlan(11, "ترفند صرفه‌جویی زمان", "آموزشی", "چطور کار ۲ ساعته رو تو ۱۰ دقیقه تموم کنی؟", "شمارش معکوس روی صفحه و تست زنده سرعت.")
            12 -> ContentDayPlan(12, "چالش ۳ روزه برای مخاطبان", "وایرال", "هر کی این چالش رو انجام بده استوریش می‌کنم!", "دعوت پرانرژی همراه با جایزه نقدی یا هدیه.")
            13 -> ContentDayPlan(13, "مسابقه حدس قیمت یا ویژگی", "گیمیفیکیشن", "فکر می‌کنی ساخت این چقدر زمان برده؟", "سوال چالشی در ۳ ثانیه اول و نوشتن حدس در کامنت.")
            14 -> ContentDayPlan(14, "بررسی اشتباهات ارسالی مخاطبان", "تعامل", "اشکال این ویدیو چیه؟ ۹۹٪ متوجه نشدن!", "توقف ویدیو روی یک فریم و هایلایت علامت سوال.")
            15 -> ContentDayPlan(15, "جشن نیمه چالش و تخفیف انفجاری", "فروش VIP", "فقط برای ۲۴ ساعت: کد تخفیف نیمه ماه!", "شمارنده معکوس و معرفی پک‌های پرفروش.", isVipOnly = true)
            else -> ContentDayPlan(
                dayNumber = day,
                title = "موضوع روز $day: سناریوی ویژه رشد و درآمد",
                category = if (day % 2 == 0) "فروش و تعامل" else "آموزش و برندینگ",
                hookIdea = "نکته پنهانی روز $day که تعامل پیج رو منفجر می‌کنه...",
                filmingTip = "ویدیو کمتر از ۳۵ ثانیه با زیرنویس بولد و موسیقی ترند ریلز.",
                isVipOnly = day > 15
            )
        }
    }
}
