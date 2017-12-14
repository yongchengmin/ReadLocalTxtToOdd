package com.print;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.StringTokenizer;
import javax.print.PrintService;

import com.runqian.report4.view.applet.AReport;

public class QuieeDirectPrintJobJava {
    private static final long serialVersionUID = 1L;

    private String appRoot;

    private String dataServlet;

    private String reportName;

    private String type;

    private String reportParams;

    private String cacheId;

    private String needPrintPrompt;

    private String needSelectPrinter;

    private String needSetPageSize;

    private String paramCharset;

    private String savePrintSetup;

    private String printServiceName;
    
    public QuieeDirectPrintJobJava(String appRoot,String reportName,String reportParams,String printServiceName) {
        init(appRoot,reportName,reportParams,printServiceName);
    }

    public void init(String appRoot,String reportName,String reportParams,String printServiceName) {

        this.appRoot = appRoot;//"http://localhost:8088/jac_fdj_wms";//http://192.168.10.92:8078/jac_ht_wms
        this.reportName = reportName;//"wmsPalletSerial.raq";//wmsTrayTag.raq
        this.reportParams = reportParams;//"id=0;ids=[43,44,45]";//230972, 230973
        this.printServiceName = printServiceName;
        
        this.dataServlet = "/reportServlet?action=1";
        this.type = "file";
        this.cacheId = null;
        this.needPrintPrompt = "no";
        this.needSelectPrinter = "no";
        this.savePrintSetup = "no";
        this.needSetPageSize = "no";
        this.paramCharset = "GBK";
    }

    public void print() {
        String s = null;
        this.reportName = this.reportName.trim();
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintService[] pss = PrinterJob.lookupPrintServices();
		//=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        Boolean isMatchprintService = false;
        if (printServiceName != null && !"".equals(printServiceName)) {
            for (PrintService ps : pss) {
                if (ps.getName().equals(printServiceName) || ps.getName().contains(printServiceName)) {
                    try {
                        job.setPrintService(ps);
                        isMatchprintService = true;
                        break;
                    }
                    catch (PrinterException e) {
                    }
                }
            }
        }
  		//=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        if(!isMatchprintService){
        	printServiceName = null;
        }
        boolean bsavePrintSetup = "yes".equalsIgnoreCase(this.savePrintSetup);
        PageFormat outerPf = null;
        int count = 1;
        if (this.reportName.startsWith("{")) {
            int index = 0;
            int len = this.reportName.length();
            while (index < len) {
                index = this.reportName.indexOf("{", index);
                if (index < 0)
                    break;
                int end = this.reportName.indexOf("}", index);
                s = this.reportName.substring(index + 1, end).trim();
                index = end + 1;
                String name = s;
                String paramString = null;
                int pos = s.indexOf("(");
                if (pos > 0) {
                    paramString = s.substring(pos + 1, s.length() - 1).trim();
                    name = s.substring(0, pos).trim();
                }
                if ((!name.toLowerCase().endsWith(".raq")) && (!name.toLowerCase().endsWith(".rat")))
                    name = name + ".raq";
                AReport report = new AReport();
                report.appRoot = this.appRoot;
                report.cachedId = this.cacheId;
                report.dataServlet = this.dataServlet;
                report.fileName = name;
                if (paramString != null) {
                    report.paramString = paramString;
                    report.paramEncode = this.paramCharset;
                }
                report.srcType = this.type;
                report.job = job;
                report.savePrintSetup = bsavePrintSetup;
                report.outerPf = outerPf;
                if ((count == 1) && (this.needSetPageSize.equalsIgnoreCase("yes"))) {
                    report.bSetPageSize = true;
                    outerPf = report.print();
                }
                else {
                    report.print();
                }
                count++;
            }
        }
        else {
            StringTokenizer st = new StringTokenizer(this.reportName, ",");
            while (st.hasMoreTokens()) {
                String name = st.nextToken().trim();
                if ((!name.toLowerCase().endsWith(".raq")) && (!name.toLowerCase().endsWith(".rat")))
                    name = name + ".raq";
                AReport report = new AReport();
                report.appRoot = this.appRoot;
                report.cachedId = this.cacheId;
                report.dataServlet = this.dataServlet;
                report.fileName = name;
                report.paramString = this.reportParams;
                report.paramEncode = this.paramCharset;
                report.srcType = this.type;
                report.job = job;
                report.savePrintSetup = bsavePrintSetup;
                report.outerPf = outerPf;
          		//=-=-=-=-=-=-=-=-=-=-=-=-=-=-
                if ((count == 1) && (this.needSetPageSize.equalsIgnoreCase("yes"))) {
                    report.bSetPageSize = true;
                    outerPf = report.print();
                }
                else {
                    report.print();
                }
                count++;
            }
        }
    }
}
