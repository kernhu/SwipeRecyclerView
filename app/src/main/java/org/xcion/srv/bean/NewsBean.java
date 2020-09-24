package org.xcion.srv.bean;

    /**
    * @Author: Kern Hu
    * @E-mail:
    * @CreateDate: 2020/9/23 13:24
    * @UpdateUser: Kern Hu
    * @UpdateDate: 2020/9/23 13:24
    * @Version: 1.0
    * @Description:
    * @UpdateRemark:
    */
    public class NewsBean {

    public static final int TYPE_LINEAR = 0;
    public static final int TYPE_GRID = 1;
    public static final int TYPE_STAGGERED_GRID1 = 2;
    public static final int TYPE_STAGGERED_GRID2 = 3;
    public static final int TYPE_STAGGERED_GRID3 = 4;

    private int type;
    private String title;

    public NewsBean(int type, String title) {
    this.type = type;
    this.title = title;
    }

    public int getType() {
    return type;
    }

    public void setType(int type) {
    this.type = type;
    }

    public String getTitle() {
    return title;
    }

    public void setTitle(String title) {
    this.title = title;
    }
    }
