前段时间看到新发布了一款App天猫读书，就安装了使用了一下，感觉主界面的首页很酷炫。然后就尝试自己做了下效果。下图是天猫读书首页的效果图。![首页效果图（压缩了下，好不清楚）](https://img-blog.csdnimg.cn/20181204144454631.gif)
开始时准备用的是viewpager来实现，后来考虑到viewpager设置动画，然后刷新的过程中会产生动画失效的问题，所幸就抛弃使用viewpager了，改用RecyclerView 实现。
天猫的首页可以分为两个大部分，一是横向滚动的列表（屏幕中心的item放大，两边的缩小），二是毛玻璃背景跟随列表切换。

**一. RecyclerView 实现ViewPager横向滚动**
横向滚动的RecyclerView 我们只需要设置LinearLayoutManager的方向就可以了。

```
 LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
         mRecyclerView.setLayoutManager(layoutManager);
```
ViewPager滚动时，每一项都是在屏幕的中间，并且以此只滑动一个Item，RecyclerView如何实现呢？滑动保持Iterm在正中间，在RecyclerView24.2.0之后，Google官方给我们提供了一个SnapHelper的辅助类，可以帮助我们实现每次滑动结束都保持在居中位置。PagerSnapHelper类是SnapHelper的一个子类，SnapHelper的另一个子类叫做LinearSnapHelper。顾名思义，两者都可以是滑动结束时item保持在正中间，但是LinearSnapHelper可以一次滑动多个item，而PagerSnapHelper像ViewPager一样限制你一次只能滑动一个item。<font color=Red>为了实现Viewpager的效果，我们这里采用PagerSnapHelper。</font>

RecyclerView 与PagerSnapHelper通过以下代码进行关联：

```
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
```
上面这段代码设置之后，RecyclerView每次只会滑动一个Item，并且会在中间显示。

第一项的左边距和最后一项的右边距是大于其他项的边距的，如果一样的话，第一项和最后一项是没有办法在中间的。如下图所示，我们需要计算第一项的左边距和最后一项的右边距。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181204151703352.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg1MzU2OA==,size_16,color_FFFFFF,t_70)
我们需要动态设置第0位置的图片的左边距为 （屏幕宽度-item宽度-item的Margin）/2，我设置的item宽度如下图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181204152001442.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg1MzU2OA==,size_16,color_FFFFFF,t_70)
官方提供了ItemDecoration的api，我们需要自定义一个类GalleryItemDecoration继承ItemDecoration，重写getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)方法，动态修改各个item的间距。以下是GalleryItemDecoration完整代码。

```
/**
 * Created by Micky on 2018/12/3.
 */
public class GalleryItemDecoration extends RecyclerView.ItemDecoration{

    int mPageMargin = 15 ;//自定义默认item边距
    int mLeftPageVisibleWidth  ;//第一张图片的左边距

    public GalleryItemDecoration(Context context)
    {
        WindowManager manager = ((Activity)context).getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        mLeftPageVisibleWidth = (int) (((width /Resources.getSystem().getDisplayMetrics().density) - 200 - mPageMargin) / 2);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int positon = parent.getChildAdapterPosition(view); //获得当前item的position
        int itemCount = parent.getAdapter().getItemCount(); //获得item的数量
        int leftMargin;
        if (positon == 0)
        {
            leftMargin = dpToPx(mLeftPageVisibleWidth);
        }else
        {
            leftMargin = dpToPx(mPageMargin);
        }
        int rightMargin;
        if (positon == itemCount - 1)
        {
            rightMargin = dpToPx(mLeftPageVisibleWidth);
        }else
        {
            rightMargin = dpToPx(mPageMargin);
        }
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
        lp.setMargins(leftMargin, 30, rightMargin, 60);
        view.setLayoutParams(lp);
        super.getItemOffsets(outRect, view, parent, state);
    }

    private int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density + 0.5f); //dp转px
    }
}
```
重写之后，然后就是代码中调用了：

```
 mRecyclerView.addItemDecoration(new GalleryItemDecoration(this));
```
效果如下图，第一项和最后一项都位于屏幕中间：
![第一项](https://img-blog.csdnimg.cn/20181204152657980.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg1MzU2OA==,size_16,color_FFFFFF,t_70)
![第二项](https://img-blog.csdnimg.cn/20181204152723885.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80Mzg1MzU2OA==,size_16,color_FFFFFF,t_70)

处理完间距问题，现在就是处理上图的放大和缩小的效果了。你会发现正中间的item是放大的效果，两边是缩放。这里我们需要在RecyclerView的OnScrollListener中进行处理。注册在父布局中一定要设置 android:clipChildren="false"，否则放大的item会被挤压。代码如下：

```
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
```
到此，横向滚动的列表我们已经完成了。接下来将处理虚化的背景了。这里我并没有通过设置ImageView来实现，而是也是通过一个RecyclerView来实现虚化背景的切换。下面是主页面的布局代码：

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clipChildren="false"
    >
   <android.support.v7.widget.RecyclerView
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       android:id="@+id/bg"/>
   <ImageView
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       android:src="@mipmap/bookshelf_card_mode_bg"
       android:scaleType="centerCrop"
       android:id="@+id/bg_top"/>
   <android.support.v7.widget.RecyclerView
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:layout_centerInParent="true"
       android:id="@+id/recyclerview"
       ></android.support.v7.widget.RecyclerView>e
</RelativeLayout>
```
同样对这个RecyclerView 设置PagerSnapHelper和LinearLayoutManager。注意这个RecyclerView是不能主动滑动的，所以我们需要屏蔽滑动事件。

```
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
```
对横向滚动列表的PagerSnapHelper的进行监听，每滑动一次就切换背景一次:

```
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
```
我这里采用的图片库是Fresco，使用的话可以上网百度。然后背景高斯模糊的代码如下：

```
  /**
     * 以高斯模糊显示。
     *
     * @param draweeView View。
     * @param url        url.
     * @param iterations 迭代次数，越大越魔化。
     * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
     */
    private void showUrlBlur(SimpleDraweeView draweeView, String url, int iterations, int blurRadius)
    {
        try {
            Uri uri = Uri.parse(url);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setPostprocessor(blurPostprocessor)
                    .build();
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(draweeView.getController())
                    .setImageRequest(request)
                    .build();
            draweeView.setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用Postprocessor实现毛玻璃图片效果
     */
    Postprocessor blurPostprocessor = new BasePostprocessor() {
        @Override
        public String getName() {
            return "blurPostprocessor";
        }

        @Override
        public void process(Bitmap bitmap) {

            int radius = 30;
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
            // 这是我自己加的，防止白屏效果
          // mCurrentBit = new BitmapDrawable(context.getResources(),bitmap);
        }
    };
```
![最终效果](https://img-blog.csdnimg.cn/20181204161547314.gif)
这样我们的这个效果就完成了。  
项目地址如下，欢迎大家fork:
