package ddwu.mobile.final_project.ma02_20190973;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class NaverLocalXmlParser {
    public enum TagType { NONE, TITLE, TELEPHONE, CATEGORY, ROADADDRESS };

    public NaverLocalXmlParser() {
    }

    public ArrayList<NaverLocalDto> parse(String xml) {

        ArrayList<NaverLocalDto> resultList = new ArrayList();
        NaverLocalDto dto = null;

        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("item")) {
                            dto = new NaverLocalDto();
                        } else if (parser.getName().equals("title")) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals("telephone")) {
                            if (dto != null) tagType = TagType.TELEPHONE;
                        } else if (parser.getName().equals("category")) {
                            if (dto != null) tagType = TagType.CATEGORY;
                        } else if (parser.getName().equals("roadAddress")) {
                            if (dto != null) tagType = TagType.ROADADDRESS;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case TELEPHONE:
                                dto.setTelephone(parser.getText());
                                break;
                            case CATEGORY:
                                dto.setCategory(parser.getText());
                                break;
                            case ROADADDRESS:
                                dto.setRoadAddress(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}

