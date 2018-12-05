package com.baijiang.www.recyclerviewpager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int mScreenWidth;
    private static final float MIN_SCALE = .95f;
    private static final float MAX_SCALE = 1.15f;
    private int mMinWidth;
    private int mMaxWidth;
    private ImageView mBgTop;
    private RecyclerView mBg;
    private RecyclerView mRecyclerView;
    private List<ItemBean> mList;
    private ItemAdapter mAdapter;
    private BgAdapter mBgAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBgTop = findViewById(R.id.bg_top);
        mBg = findViewById(R.id.bg);
        mRecyclerView = findViewById(R.id.recyclerview);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mMinWidth = (int) (mScreenWidth * 0.28f);
        mMaxWidth = mScreenWidth - 2 * mMinWidth;

        mList = new ArrayList<>();
        mAdapter = new ItemAdapter(this,mList);
        mBgAdapter = new BgAdapter(this,mList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置不可滑动
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this)
        {
            @Override
            public boolean canScrollHorizontally() {
                // 屏蔽滑动事件
                return false;
            }
        };
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBg.setLayoutManager(layoutManager2);
        mBg.setAdapter(mBgAdapter);
        PagerSnapHelper pagerSnapHelper2 = new PagerSnapHelper();
        pagerSnapHelper2.attachToRecyclerView(mBg);



        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new GalleryItemDecoration(this));
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper(){
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                mBg.scrollToPosition(targetPos);
             //   mBg.scrollTo(velocityX,velocityY);
                Log.d("PageSnap","位置："+targetPos);
                return targetPos;
            }
        };
        pagerSnapHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 移动bg
                Log.d("MICKY","移动的距离："+dx);
                final int childCount = recyclerView.getChildCount();
                Log.e("tag", childCount + "");
                for (int i = 0; i < childCount; i++) {
                    LinearLayout child = (LinearLayout) recyclerView.getChildAt(i);
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
                    // lp.rightMargin = 5;
                    // lp.height = 200;


                    int left = child.getLeft();
                    int right = mScreenWidth - child.getRight();
                    final float percent = left < 0 || right < 0 ? 0 : Math.min(left, right) * 1f / Math.max(left, right);
                    Log.d("Wumingtag", "percent = " + percent+";位置："+i);
                    float scaleFactor = MIN_SCALE + Math.abs(percent) * (MAX_SCALE - MIN_SCALE);
                    int width = (int) (mMinWidth + Math.abs(percent) * (mMaxWidth - mMinWidth));
                    // lp.width = width;
                    Log.d("Wumingtag", "scaleFactor = " + scaleFactor+";位置："+i);
                    child.setLayoutParams(lp);
                    child.setScaleY(scaleFactor);
                    child.setScaleX(scaleFactor);
                   // child.setBackground(getD);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        // 设置遮罩层
        mBgTop.setImageBitmap(fastblur(BitmapFactory.decodeResource(getResources(), R.mipmap.bookshelf_card_mode_bg),30));
        // 设置遮罩层透明度
        mBgTop.setAlpha(0.7f);
        mBgTop.setVisibility(View.GONE);
        addTest();
    }

    private void addTest()
    {
        mList.add(new ItemBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1544083522&di=b0b401fcb1a0fc8378ff862e35d58294&imgtype=jpg&er=1&src=http%3A%2F%2Fmf19.yifutu.com%2Fmoban%2F1508%2F25%2F11%2FE2_744292_3470c9f9-9060-40e2-8b9a-8723d7bb76c1.Png","《泰坦尼克号》"));
        mList.add(new ItemBean("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2764082072,3017850062&fm=26&gp=0.jpg","《小萝莉的猴神大叔》"));
        mList.add(new ItemBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543489058357&di=8d68a6394408ace2cb6d8711fe28060b&imgtype=0&src=http%3A%2F%2Fpic13.photophoto.cn%2F20091114%2F0036036372535927_b.jpg","《老巫师》"));
        mList.add(new ItemBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543489083134&di=b0767623e630e35d3515418c12d05414&imgtype=0&src=http%3A%2F%2Fpic158.nipic.com%2Ffile%2F20180314%2F19136215_155608851038_2.jpg","《红海行动》"));
        mList.add(new ItemBean("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1464869334,620617080&fm=26&gp=0.jpg","《一出好戏》"));
        mList.add(new ItemBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1544083835&di=2e234d20a9a242800230610fa44fe95e&imgtype=jpg&er=1&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn03%2F690%2Fw640h850%2F20180826%2Fec69-hifuvpf7976046.jpg","《红海行动》"));
        mList.add(new ItemBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1543489165803&di=067e34d778985d37027a49e989a1338a&imgtype=0&src=http%3A%2F%2Ftaobao.90sheji.com%2F58pic%2F13%2F91%2F07%2F51Q58PICTUA.jpg","《浮城大亨》"));

        mAdapter.notifyDataSetChanged();
        mBgAdapter.notifyDataSetChanged();
    }

    /**
     * 实现高斯模糊
     * @param sentBitmap
     * @param radius
     * @return
     */
    public Bitmap fastblur(Bitmap sentBitmap, int radius) {

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }


}
