package com.paxing.test.kaoqin.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * ClassName:  Pagination. <br/>
 * Description:  分页基础类 <br/>
 *
 * @author xblibo
 * 2013-3-2 下午12:37:35 <br/>
 * @version 1.0
 */
public class Pagination<T extends Serializable> {
    /**
     * 默认的每页数据量（pageSize）
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    /**
     * 默认页码（第一页）
     */
    public static final int DEFAULT_PAGE_NUM = 1;
    /**
     * 默认显示页码标签的个数 如： {首页  1 2 3 4 5 ... 16 17 18 末页}
     */
    public static final int DEFAULT_MAX_PAGE_INDEX_NUMBER = 9;
    /**
     * 显示页码标签的个数
     */
    private int maxPageIndexNumber = DEFAULT_MAX_PAGE_INDEX_NUMBER;
    /**
     * 页码编号数组
     */
    protected int[] pageNumberList = new int[0];
    /**
     * 需分页的数据总量
     */
    protected int totalCount;
    /**
     * 每页数据量
     */
    protected int pageSize = DEFAULT_PAGE_SIZE;
    /**
     * 总页数
     */
    protected int totalPage;
    /**
     * 当前页码
     */
    protected int currentPage;
    /**
     * 下一页页码
     */
    protected int nextPage;
    /**
     * 上一页页码
     */
    protected int previousPage;
    /**
     * 是否有下一页
     */
    protected boolean hasNext = false;
    /**
     * 是否有前一页
     */
    protected boolean hasPrevious = false;
    /**
     * 获取该页的数据列表
     */
    protected List<T> list = new ArrayList<>();
    /**
     *
     */
    protected int startIndex;

    public Pagination() {
        super();
    }

    public Pagination(int currentPage, int pageSize) {
        if (currentPage <= 0) {
            currentPage = 1;
        }
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.startIndex = (currentPage - 1) * pageSize;
    }

    public Pagination(int totalCount) {
        this(totalCount, totalCount);
    }

    public Pagination(int totalCount, int pageSize, int currentPage) {
        this(totalCount, pageSize, currentPage, DEFAULT_MAX_PAGE_INDEX_NUMBER);
    }

    public Pagination(int totalCount, int pageSize, int currentPage, int maxPageIndexNumber) {
        this.maxPageIndexNumber = maxPageIndexNumber;
        init(totalCount, pageSize, currentPage);
    }

    /**
     * 计算当前页的取值范围：pageStartRow和pageEndRow
     */
    private void calculatePage() {
        //计算总页数
        if ((totalCount % pageSize) == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        //判断是否还有上一页
        hasPrevious = (currentPage - 1) > 0;

        //判断是否还有下一页
        hasNext = currentPage < totalPage;
        //计算上一页
        if (hasPrevious) {
            previousPage = currentPage - 1;
        }
        //计算下一页
        if (hasNext) {
            nextPage = currentPage + 1;
        }
    }

    public void init(int totalCount, int pageSize, int currentPage) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        calculatePage();
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public int getNextPage() {
        return nextPage;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    /**
     * 获取页码标签列表大小 <br/>
     */
    public int getMaxPageIndexNumber() {
        return maxPageIndexNumber;
    }

    /**
     * 设置页码标签列表大小 <br/>
     */
    public void setMaxPageIndexNumber(int maxPageIndexNumber) {
        this.maxPageIndexNumber = maxPageIndexNumber;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * 获取页码列表 <br/>
     *
     * @return
     * @author xblibo
     * 2013-3-2 下午3:42:38
     */
    public int[] getPageNumberList() {
        if (totalPage > this.maxPageIndexNumber) {
            this.pageNumberList = new int[this.maxPageIndexNumber];
            int offset = (this.maxPageIndexNumber - 4) / 2;
            if (this.currentPage - offset <= (1 + 2)) {
                for (int index = 0; index < maxPageIndexNumber - 2; index++) {
                    pageNumberList[index] = (index + 1);
                }
            } else if (this.currentPage + offset >= (totalPage - 2)) {
                int start = totalPage;
                for (int index = maxPageIndexNumber - 1; index > 1; index--) {
                    pageNumberList[index] = (start--);
                }
            } else {
                int start = currentPage - offset;
                for (int index = 2; index < maxPageIndexNumber - 2; index++) {
                    pageNumberList[index] = (start++);
                }
            }
            pageNumberList[0] = 1;
            pageNumberList[maxPageIndexNumber - 1] = totalPage;
        } else {//总页数小于 设置的页码标签数
            this.pageNumberList = new int[this.totalPage];
            for (int index = 0; index <= totalPage - 1; index++) {
                pageNumberList[index] = (index + 1);
            }
        }
        return pageNumberList;
    }


    public static void main(String[] args) {
        Pagination<Integer> pagination = new Pagination<Integer>(200, 10, 5);
        for (int i : pagination.getPageNumberList()) {
            System.out.print(i + "\t");
        }
    }

    /**
     * 获取上一页 <br/>
     *
     * @return
     * @author CY6382
     * 2014-9-17
     */

    public String getHasPrevious() {
        return hasPrevious() ? "true" : "false";
    }

    /**
     * 获取下一页 <br/>
     *
     * @return
     * @author CY6382
     * 2014-9-17
     */

    public String getHasNext() {
        return hasNext() ? "true" : "false";
    }

    /**
     * TODO <br/>
     *
     * @author CY6382
     * 2014-9-24
     */

    public int getStartIndex() {
        return this.startIndex;
    }

    /**
     * TODO <br/>
     *
     * @param startIndex
     * @author CY6382
     * 2014-9-24
     */

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setHasNext(String s) {

        this.hasNext = (Objects.equals(s, "true") ? true : false);

    }

    public void setHasPrevious(String s) {

        this.hasPrevious = (Objects.equals(s, "true") ? true : false);

    }

    public void setTotalPage(int size) {

        this.totalPage = size;

    }

    public void setNextPage(int nextPage) {

        this.nextPage = nextPage;

    }

    public void setPreviousPage(int previousPage) {

        this.previousPage = previousPage;

    }
}

