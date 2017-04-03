package com.cyou.video.mobile.server.cms.model.collection;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import com.tencent.xinge.TagTokenPair;

public class UserItemOperatePvMongo {

  List<TagTokenPair> iosTags = new ArrayList<TagTokenPair>();

  List<TagTokenPair> androidTags = new ArrayList<TagTokenPair>();

  List<TagTokenPair> iosDelTags = new ArrayList<TagTokenPair>();

  List<TagTokenPair> androidDelTags = new ArrayList<TagTokenPair>();

  public void addIosTag(TagTokenPair tag) {
    iosTags.add(tag);
  }

  public void addIosDelTag(TagTokenPair tag) {
    iosDelTags.add(tag);
  }

  public void addAndroidTag(TagTokenPair tag) {
    androidTags.add(tag);
  }

  public void addAndroidDelTag(TagTokenPair tag) {
    androidDelTags.add(tag);
  }

  
  public List<TagTokenPair> getIosTags() {
    return iosTags;
  }

  public void setIosTags(List<TagTokenPair> iosTags) {
    this.iosTags = iosTags;
  }

  public List<TagTokenPair> getAndroidTags() {
    return androidTags;
  }

  public void setAndroidTags(List<TagTokenPair> androidTags) {
    this.androidTags = androidTags;
  }

  public List<TagTokenPair> getIosDelTags() {
    return iosDelTags;
  }

  public void setIosDelTags(List<TagTokenPair> iosDelTags) {
    this.iosDelTags = iosDelTags;
  }

  public List<TagTokenPair> getAndroidDelTags() {
    return androidDelTags;
  }

  public void setAndroidDelTags(List<TagTokenPair> androidDelTags) {
    this.androidDelTags = androidDelTags;
  }




}
