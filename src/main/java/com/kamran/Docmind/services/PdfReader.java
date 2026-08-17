package com.kamran.Docmind.services;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class PdfReader {
    private final FileStorageServices fileStorageServices;

    public PdfReader(FileStorageServices fileStorageServices) {
        this.fileStorageServices = fileStorageServices;
    }

    public List<Document> readDocument(Long id){

        String path = fileStorageServices.getFilePathById(id);

        System.out.println("*************************************************************");
        System.out.println("Path : "+path);
        System.out.println("*************************************************************");

        Resource resource=new FileSystemResource(path);

        
        return readDocument(resource);

        // PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(path, PdfDocumentReaderConfig.builder()
        //         .withPageTopMargin(0)
        //         .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
        //                 .withNumberOfTopTextLinesToDelete(0)
        //                 .build())
        //         .withPagesPerDocument(1)
        //         .build());
        // return pdfReader.read();
    }


    public List<Document> readDocument(Resource resource){

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build());
        return pdfReader.read();
    }



}
