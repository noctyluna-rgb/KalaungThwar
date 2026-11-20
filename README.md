# ကလောင်သွား – Android APK Project

ဒီ Project ထဲမှာ မူရင်း HTML App ကို `app/src/main/assets/index.html` အဖြစ် ထည့်ထားပါတယ်။

## GitHub မှ APK ထုတ်ရန်
1. ZIP ကို ဖြည်ပြီး အတွင်းက ဖိုင်/ဖိုလ်ဒါအားလုံးကို GitHub repository ရဲ့ root ထဲ Upload လုပ်ပါ။
2. `Actions` ကိုဖွင့်ပါ။
3. `Build KalaungThwar APK` workflow ကိုရွေးပြီး `Run workflow` နှိပ်ပါ။ `main` သို့မဟုတ် `master` branch ကို push လုပ်ရင်လည်း အလိုအလျောက် build ပါမယ်။
4. Build အောင်မြင်လျှင် workflow run အောက်က **Artifacts** မှ `KalaungThwar-debug-apk` ကို Download လုပ်ပါ။
5. ZIP ဖြည်ပြီး `app-debug.apk` ကို Android ဖုန်းမှာ install လုပ်ပါ။

## ပါဝင်သော အင်္ဂါရပ်
- မူရင်း HTML စာရေး App ကို WebView ထဲမှာ ဖွင့်ခြင်း
- JavaScript နှင့် localStorage ဖွင့်ထားခြင်း (စာမူ/စာအုပ်ဒေတာကို ဖုန်းထဲတွင် သိမ်းရန်)
- Word export ဖိုင်ကို Downloads/KalaungThwar ထဲသိမ်းရန်
- PDF export အတွက် Android Print dialog ဖွင့်ရန်
- App အတွက် အင်တာနက်ခွင့်ပြုချက်

## သတိပြုရန်
မူရင်း HTML မှာ Tailwind CSS, Lucide icons နှင့် Google Fonts ကို CDN ကနေ ခေါ်သုံးထားပါတယ်။ ဒါကြောင့် UI အပြည့်အဝပေါ်ဖို့ အင်တာနက်လိုနိုင်ပါတယ်။ စာရေးဒေတာကိုတော့ localStorage ဖြင့် ဖုန်းထဲမှာ သိမ်းထားပါတယ်။ App data ကို Clear data လုပ်ခြင်း သို့မဟုတ် App ကို uninstall လုပ်ခြင်းမပြုမီ စာမူတွေကို Word/PDF အဖြစ် Export လုပ်ထားပါ။
