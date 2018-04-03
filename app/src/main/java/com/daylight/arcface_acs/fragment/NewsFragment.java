package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.app.GlideApp;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView;

/**
 * 公告详情界面
 * Created by Daylight on 2018/3/23.
 */

public class NewsFragment extends BaseFragment {
    private View view;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_news,null);
        initTopBar();
        initLinearLayout();
        return view;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_news);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.news);
    }
    @SuppressLint("SetTextI18n")
    private void initLinearLayout(){
        TextView title=view.findViewById(R.id.news_title);
        title.setText("清明节祭祖防火安全通知");
        TextView time=view.findViewById(R.id.news_time);
        time.setText("2018年03月22日");
        ImageView image=view.findViewById(R.id.news_image);
        GlideApp.with(getBaseFragmentActivity()).load("http://imgsrc.baidu.com/imgad/pic/item/4b90f603738da97734fa5c06bb51f8198718e3c2.jpg")
                .centerCrop().into(image);
        QMUISpanTouchFixTextView text=view.findViewById(R.id.news_text);

        text.setText("\u3000\u3000清明节是中华民族缅怀先人、祭祀祖先的传统节日，今年的清明节正好碰上周末假期，又是一个三天小长假，加之气温转暖，群众扫墓祭祖、焚香烧纸活动频繁，踏青出游增多。为确保广大市民渡过一个平安、祥和、文明的清明节，在此消防部门温馨提示：清明节期间切不可忽视消防安全，做好必要的安全出行和防火措施。\n" +
                "\u3000\u3000一、注意家庭安全。出行前，应仔细检查用火、用电、用气、用水安全，务必确认切断家中电源，关闭燃气(煤气)开关，关好水龙头，防止家庭火灾及其他事故发生。保管好现金、首饰等贵重物品，出门前关好门窗、锁好房门，以防失窃。\n" +
                "\u3000\u3000二、注意交通安全。清明节期间车多人多，为了不造成交通拥堵，避免出现意外交通事故，建议出行人员避开高峰时段，错时出行。出行时自觉遵守交通规则，保持适当车速和车距，不饮酒驾车，不疲劳驾驶，不违规超车，不乱停车，文明出行。\n" +
                "\u3000\u3000三、注意防火安全。春天风大物燥，扫墓祭祖时，切勿在山林、草地乱扔烟头、燃放爆竹和焚香烧纸，尽量选择鲜花祭祖。如果一定要使用明火，务必选择空旷无燃烧物的地方。祭扫完毕后，要确认余火完全熄灭后才离开，最好随身携带矿泉水浇灭火星，以防疏忽造成火灾。\n" +
                "\u3000\u3000四、注意人生安全。清明节期间，天气多变，勿晴勿雨，如遇雨天，在登山时，尽量做好防滑措施，不要走陡坡小路，以免滑倒跌伤。在野外如遇雷雨天气，切记不要接打手机，不要靠近大树、电杆，不要接触金属、电线，不要停留应及时躲避，以免被雷击伤。");
    }
}
