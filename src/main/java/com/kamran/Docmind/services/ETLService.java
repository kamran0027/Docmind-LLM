package com.kamran.Docmind.services;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ETLService {
    private final FileStorageServices fileStorageServices;

    private final TokenTextSplitter splitter;

    private final VectorDatabseServices vectorDatabseServices;

    public ETLService(FileStorageServices fileStorageServices,VectorDatabseServices vectorDatabseServices) {
        this.fileStorageServices = fileStorageServices;
        this.vectorDatabseServices=vectorDatabseServices;
        this.splitter=TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(350)
                .withPunctuationMarks(List.of('。', '？', '！', '；'))
                .build();
    }

    public List<Document> readDocument(String documentId){

        String path = fileStorageServices.getFilePathById(documentId);

        System.out.println("*************************************************************");
        System.out.println("Path : "+path);
        System.out.println("*************************************************************");

        Resource resource=new FileSystemResource(path);

        
        return readDocument(resource,documentId);

        // PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(path, PdfDocumentReaderConfig.builder()
        //         .withPageTopMargin(0)
        //         .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
        //                 .withNumberOfTopTextLinesToDelete(0)
        //                 .build())
        //         .withPagesPerDocument(1)
        //         .build());
        // return pdfReader.read();
    }


    public List<Document> readDocument(Resource resource,String documentId){

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build());
        List<Document> pages=pdfReader.read();

        // 2. Add metadata to every page
        pages.forEach(page -> {
            page.getMetadata().put("documentId",documentId);
            page.getMetadata().put(
                    "fileName",
                    resource.getFilename()
            );
        });

        List<Document> documents=splitDocumnet(pages);

        vectorDatabseServices.addDocuments(documents);

        return documents;
    }


    public List<Document> splitDocumnet(List<Document> documents){

        return splitter.apply(documents);

    }



}
