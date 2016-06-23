package eu.europeana.inspire.model;

import eu.europeana.common.Tools;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by cmos on 23.06.16.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {
    private int uniqueTileCount;
    private String link;

    public Result() {
    }

    public Result(int uniqueTileCount, String link) {
        this.uniqueTileCount = uniqueTileCount;
        this.link = link;
    }

    public int getUniqueTileCount() {
        return uniqueTileCount;
    }

    public void setUniqueTileCount(int uniqueTileCount) {
        this.uniqueTileCount = uniqueTileCount;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        try {
            return Tools.marshallAny(this);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "";
    }
}
