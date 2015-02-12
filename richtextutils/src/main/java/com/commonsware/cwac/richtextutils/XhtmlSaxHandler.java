/***
 Copyright (c) 2015 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.commonsware.cwac.richtextutils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Arrays;
import java.util.Stack;

class XhtmlSaxHandler extends DefaultHandler {
  private static final String[] NO_ITEM_TAGS={"br", "div"};
  private final SpanTagRoster tagRoster;
  private Stack<Item> textStack=new Stack<Item>();

  XhtmlSaxHandler(SpanTagRoster tagRoster) {
    this.tagRoster=tagRoster;

    textStack.push(new Item(null, ""));
  }

  Spannable getContent() {
    return(textStack.peek().getContent());
  }

  public void startElement(String uri, String localName,
                           String name, Attributes a) {
    CharacterStyle span=tagRoster.buildSpanForTag(name, a);

    if (Arrays.binarySearch(NO_ITEM_TAGS, name)<=0) {
      textStack.push(new Item(span, ""));
    }
  }

  public void endElement(String uri, String localName, String name) {
    if ("div".equals(name)) {
      textStack.peek().append("\n\n");
    }
    else if ("br".equals(name)) {
      textStack.peek().append("\n");
    }
    else {
      Item toFinish=textStack.pop();
      Item theNewTop=textStack.peek();

      theNewTop.append(toFinish);
    }
  }

  public void characters(char[] ch, int start, int length) {
    textStack.peek().append(new String(ch, start, length));
  }

  public InputSource resolveEntity(String publicId, String systemId)
      throws org.xml.sax.SAXException, java.io.IOException {
    throw new IllegalStateException("Entities are not supported!");
  }

  private static class Item {
    private final CharacterStyle activeSpan;
    private final SpannableStringBuilder content;

    Item(CharacterStyle span, CharSequence initialContent) {
      activeSpan=span;
      content=new SpannableStringBuilder(initialContent);
    }

    void append(CharSequence newContent) {
      content.append(newContent);
    }

    void append(Item item) {
      int start=content.length();

      content.append(item.content);
      content.setSpan(item.activeSpan, start, content.length(),
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    Spannable getContent() {
      return(content);
    }
  }
}
