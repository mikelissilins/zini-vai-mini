package lv.zinivaimini.game.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "games")
public class Game {
    @Id
    private UUID id;
    private String title;
    private String description;
    private String locale;
    private boolean template;
    private String templateKey;
    @Version
    private long version;
    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Category> categories = new ArrayList<>();

    protected Game() {
    }

    public Game(String title, String description, String locale) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.locale = locale;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void update(String title, String description, String locale) {
        this.title = title;
        this.description = description;
        this.locale = locale;
    }

    public void replaceCategories(List<Category> replacements) {
        categories.clear();
        replacements.forEach(this::addCategory);
    }

    public void addCategory(Category category) {
        category.attachTo(this);
        categories.add(category);
        categories.sort(Comparator.comparingInt(Category::getPosition));
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocale() { return locale; }
    public boolean isTemplate() { return template; }
    public String getTemplateKey() { return templateKey; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<Category> getCategories() { return List.copyOf(categories); }
}
