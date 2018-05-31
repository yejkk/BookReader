package com.justwayward.reader.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.justwayward.reader.R;
import com.justwayward.reader.bean.BookChapterComRespo;
import com.justwayward.reader.utils.FileUtils;
import com.yuyh.easyadapter.abslistview.EasyLVAdapter;
import com.yuyh.easyadapter.abslistview.EasyLVHolder;

import java.util.List;

public class CommenListAdapter extends EasyLVAdapter<BookChapterComRespo.BookChapter> {

    private CommenBookListener commenBookListener;

    private List<BookChapterComRespo.BookChapter> list;

    private EditText tvTocItem;

    public interface CommenBookListener {
        void sendCommen(String info);
    }

    public CommenListAdapter(Context context, List<BookChapterComRespo.BookChapter> list, CommenBookListener commenBookListener ) {
        super(context, list, R.layout.item_book_commen_bottom,R.layout.item_book_commen_list);
        this.list = list;
        this.commenBookListener = commenBookListener;
    }

    @Override
    public void convert(EasyLVHolder holder, int position, BookChapterComRespo.BookChapter s) {

        if(position == 0){
            tvTocItem = holder.getView(R.id.commen_write_commen);
            Button button = holder.getView(R.id.commen_commen_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commenBookListener.sendCommen(tvTocItem.getText().toString());
                }
            });
        }else{
            TextView commen_name = holder.getView(R.id.commen_name);
            TextView commen_info = holder.getView(R.id.commen_info);
            commen_name.setTextColor(ContextCompat.getColor(mContext, R.color.light_black));
            commen_info.setTextColor(ContextCompat.getColor(mContext, R.color.light_black));
            commen_name.setText(list.get(position-1).userName);
            commen_info.setText(list.get(position-1).info);
            Drawable drawable;
            drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_toc_item_normal);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            commen_name.setCompoundDrawables(drawable, null, null, null);
        }
    }

    @Override
    public int getLayoutIndex(int position, BookChapterComRespo.BookChapter item){
        if(position == 0){
            return layoutIds[0];
        }else{
            return layoutIds[1];
        }
    }
}
