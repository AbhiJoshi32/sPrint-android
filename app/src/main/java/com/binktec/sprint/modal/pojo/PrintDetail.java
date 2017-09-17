package com.binktec.sprint.modal.pojo;

public class PrintDetail {

    private String bindingType;
    private String printColor;
    private int printCopies;
    private String printNoPages;
    private String printOrientation;
    private String printPaperType;
    private int copies;
    private int pagesPerSheet;
    private int pagesToPrint;
    private String pagesText;

    public int getPagesToPrint() {
        return pagesToPrint;
    }

    public void setPagesToPrint(int pagesToPrint) {
        this.pagesToPrint = pagesToPrint;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public int getPagesPerSheet() {
        return pagesPerSheet;
    }

    public void setPagesPerSheet(int pagesPerSheet) {
        this.pagesPerSheet = pagesPerSheet;
    }

    public String getPagesText() {
        return pagesText;
    }

    public void setPagesText(String pagesText) {
        this.pagesText = pagesText;
    }

    public String getBindingType() {
        return bindingType;
    }

    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
    }

    public String getPrintColor() {
        return printColor;
    }

    public void setPrintColor(String printColor) {
        this.printColor = printColor;
    }

    public int getPrintCopies() {
        return printCopies;
    }

    public void setPrintCopies(int printCopies) {
        this.printCopies = printCopies;
    }

    public String getPrintNoPages() {
        return printNoPages;
    }

    public void setPrintNoPages(String printNoPages) {
        this.printNoPages = printNoPages;
    }

    public String getPrintOrientation() {
        return printOrientation;
    }

    public void setPrintOrientation(String printOrientation) {
        this.printOrientation = printOrientation;
    }

    public String getPrintPaperType() {
        return printPaperType;
    }

    public void setPrintPaperType(String printPaperType) {
        this.printPaperType = printPaperType;
    }

}
