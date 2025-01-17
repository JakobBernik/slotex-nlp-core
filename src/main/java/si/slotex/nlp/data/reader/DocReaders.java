package si.slotex.nlp.data.reader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLStreamException;

public class DocReaders
{
    public static List<Doc> getDoc(String path) throws ZipException, IOException, XMLStreamException
    {
        File f = new File(path);
        List<Doc> docs = new ArrayList<Doc>();
        if (f.isDirectory())
        {
            File[] files = f.listFiles(new FileFilter()
            {
                @Override
                public boolean accept(File arg0)
                {
                    return arg0.getName().endsWith(".zip") || arg0.getName().endsWith(".xml") || arg0.getName().endsWith(".tsv");
                }
            });
            for (File fi : files)
            {
                docs.addAll(openFile(fi));
            }
        }
        return null;
    }

    public static List<Doc> openFile(File f) throws ZipException, IOException, XMLStreamException
    {
        List<Doc> docs = new ArrayList<Doc>();
        if (f.getName().endsWith(".zip"))
        {
            ZipFile zf = new ZipFile(f);
            Enumeration<? extends ZipEntry> zfiles = zf.entries();
            while (zfiles.hasMoreElements())
            {
                ZipEntry ze = zfiles.nextElement();
                InputStream zis = zf.getInputStream(ze);
                String fNm = ze.getName();
                openStream(docs, zis, fNm);
            }
            zf.close();
        }
        else
        {
            InputStream fis = new FileInputStream(f);
            openStream(docs, fis, f.getName());
        }

        return docs;
    }

    public static List<Doc> openDir(File dir) throws ZipException, IOException, XMLStreamException
    {
        List<Doc> docs = new ArrayList<Doc>();

        for (File f : dir.listFiles())
        {
            docs.addAll(openFile(f));
        }
        return docs;
    }

    private static void openStream(List<Doc> docs, InputStream zis, String fNm)
            throws XMLStreamException, IOException
    {
        if (fNm.endsWith(".xml"))
        {
            InputStream is = new BufferedInputStream(zis);
            docs.add(openTEIStream(is));
            is.close();
        }
        else if (fNm.endsWith(".tsv"))
        {
            InputStream is = new BufferedInputStream(zis);
            docs.add(openTsvStream(is));
            is.close();
        }
    }

    public static Doc openTsvStream(InputStream inputStream) throws IOException
    {
        TsvReader rdr = new TsvReader();
        Doc d = rdr.read(inputStream);
        return d;
    }

    public static Doc openTEIStream(InputStream inputStream) throws XMLStreamException
    {
        TEIReader rdr = new TEIReader();
        Doc d = rdr.read(inputStream);
        return d;
    }
}
