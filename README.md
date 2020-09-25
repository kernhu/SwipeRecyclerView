# SwipeRecyclerView
SwipeRecyclerView is a custom RecyclerView who can pull to refresh both and pull to load more.
It is easy to be used. it also can custom loading page/empty page and error page.

SwipeRecyclerView 是一个非入侵Adapter 且自带下拉刷新，上拉加载更多，支持自定义加载页、错误页、无数据页 的RecyclerView。

![](https://github.com/KernHu/Stamper/raw/master/screenshot/screenshot1.png)
![](https://github.com/KernHu/Stamper/raw/master/screenshot/screenshot2.png)

##  I: How to use SwipeRecyclerView.

```
<com.xcion.lib.SwipeRecyclerView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/load_refresh_recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:srv_autoCompleteLoadingMore="true"
    app:srv_autoCompleteRefreshing="true"
    app:srv_clipToPadding="true"
    app:srv_empty_layout="@layout/srv_layout_empty_view"
    app:srv_error_layout="@layout/srv_layout_error_view"
    app:srv_loading_layout="@layout/srv_layout_loading_view"
    app:srv_loadmore_layout="@layout/srv_layout_loadmore_view"
    app:srv_paddingLeft="10px"
    app:srv_paddingRight="10px"
    app:srv_scrollbar_enable="true" />
```

### 2.use it stamp a image;

```

```
### 3.the callback

```
StampWatcher mStampWatcher = new StampWatcher() {
        @Override
        protected void onSuccess(Bitmap bitmap, int requestId) {
            super.onSuccess(bitmap, requestId);

            switch (requestId) {

                case 1001:
                    //the result of text stamper

                    break;
                case 1002:
                    //the result of image stamper

                    break;
            }
        }

        @Override
        protected void onError(String error, int requestId) {
            super.onError(error, requestId);

            switch (requestId) {

                case 1001://

                    break;
                case 1002://

                    break;
            }
        }
    };

```

## II: Add Stamper to your project

### Step 1. Add the JitPack repository to your build file; Add it in your root build.gradle at the end of repositories:
```
	allprojects {
        repositories {
        ...
        maven { url 'https://jitpack.io' }
        }
        }
```
### Step 2. Add the dependency
```
	dependencies {
	         implementation 'com.github.KernHu:Stamper:1.1'
	}
```
## III: Contact me

Email: vsky580@gmail.com
Facebook: https://www.facebook.com/kern.hu.580

QQ群：43447852

I'm kern....

If it helps you,please give me a star.如果有帮助到你，请给我一个小星星。

