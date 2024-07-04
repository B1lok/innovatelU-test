package org.example;

import java.util.*;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

  private final Map<String, Document> documentStore = new HashMap<>();

  /**
   * Implementation of this method should upsert the document to your storage
   * And generate unique id if it does not exist
   *
   * @param document - document content and author data
   * @return saved document
   */
  public Document save(Document document) {
    if (document.getId() == null) {
      document.setId(UUID.randomUUID().toString());
    }
    documentStore.put(document.getId(), document);
    return document;
  }

  /**
   * Implementation this method should find documents which match with request
   *
   * @param request - search request, each field could be null
   * @return list matched documents
   */
  public List<Document> search(SearchRequest request) {
    return documentStore.values().stream()
        .filter(doc -> matchesSearchCriteria(doc, request))
        .toList();
  }

  /**
   * Implementation this method should find document by id
   *
   * @param id - document id
   * @return optional document
   */
  public Optional<Document> findById(String id) {
    return Optional.ofNullable(documentStore.get(id));
  }

  private boolean matchesSearchCriteria(Document document, SearchRequest request) {
    if (request == null) {
      return true;
    }

    if (isNotNullOrEmpty(request.getTitlePrefixes())) {
      boolean matchTitlePrefix = request.getTitlePrefixes().stream()
          .anyMatch(prefix -> document.getTitle().startsWith(prefix));
      if (!matchTitlePrefix) {
        return false;
      }
    }

    if (isNotNullOrEmpty(request.getContainsContents())) {
      boolean matchContent = request.getContainsContents().stream()
          .anyMatch(content -> document.getContent().contains(content));
      if (!matchContent) {
        return false;
      }
    }

    if (isNotNullOrEmpty(request.getAuthorIds())) {
      if (!request.getAuthorIds().contains(document.getAuthor().getId())) {
        return false;
      }
    }

    if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
      return false;
    }
    if (request.getCreatedTo() != null && document.getCreated().isAfter(request.getCreatedTo())) {
      return false;
    }

    return true;
  }

  private boolean isNotNullOrEmpty(List<?> list) {
    return list != null && !list.isEmpty();
  }

  @Data
  @Builder
  public static class SearchRequest {
    private List<String> titlePrefixes;
    private List<String> containsContents;
    private List<String> authorIds;
    private Instant createdFrom;
    private Instant createdTo;
  }

  @Data
  @Builder
  public static class Document {
    private String id;
    private String title;
    private String content;
    private Author author;
    private Instant created;
  }

  @Data
  @Builder
  public static class Author {
    private String id;
    private String name;
  }
}