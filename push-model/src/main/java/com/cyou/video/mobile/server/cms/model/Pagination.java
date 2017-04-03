package com.cyou.video.mobile.server.cms.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;


/**
 * 分页封装类
 * @author jyz
 */
public class Pagination  {

  public static final int PAGESIZE = 10; //默认每页10条记录

  private int curPage = 1; //当前页码

  private int pageSize = PAGESIZE; //每页条数

  private List<?> content; //记录集

  private int rowCount = 0; //记录数

  public int getCurPage() {
    return curPage;
  }

  public void setCurPage(int curPage) {
    this.curPage = curPage;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public List<?> getContent() {
    return content;
  }

  public void setContent(List<?> content) {
    this.content = content;
  }

  public int getRowCount() {
    return rowCount;
  }

  public void setRowCount(int rowCount) {
    this.rowCount = rowCount;
  }

  public int getPageCount() {
    if(rowCount == 0) {
      return 0;
    }
    else if(curPage < 0) {
      if(content != null && content.size() > 0) {
        return 1;
      }
      else {
        return 0;
      }
    }
    else {
      return rowCount / pageSize + (rowCount % pageSize > 0 ? 1 : 0);
    }
  }

  public int getStartRow() {
    if((content != null && content.size() > 0)||rowCount>0) {
      if(curPage < 0) {
        return 1;
      }
      else {
        return (curPage - 1) * pageSize + 1;
      }
    }
    else {
      return 0;
    }
  }

  public int getEndRow() {
    if(content != null && content.size() > 0) {
      return getStartRow() + content.size();
    }
    else {
      return rowCount>0?getStartRow() + rowCount:0;
    }
  }


  
}
