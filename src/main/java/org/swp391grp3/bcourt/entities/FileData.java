package org.swp391grp3.bcourt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "file", schema = "bcourt")
public class FileData {

    @Id
    @UuidGenerator
    @Column(name = "fileId", updatable = false, nullable = false, length = 36)
    private String fileId;

    @Column(name = "fileName", length = 255)
    private String fileName;

    @Column(name = "fileUrl")
    private String fileUrl;

    @Column(name = "fileType", length = 100)
    private String fileType;

    //@OneToMany(mappedBy = "file")
    //private Set<User> users = new LinkedHashSet<>();
}
