package jungmo.server.domain.entity;

import jakarta.persistence.*;
import jungmo.server.domain.dto.response.UserResponse;
import jungmo.server.global.auditing.BaseTimeEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false, unique = true, length = 6)
    private String userCode;

    private String userName;

    private String email;

    private String password;
    @Column(name = "user_role")
    private String role;

    private String profileImage;

    private String provider;

    private String oauthId;

    private Boolean isDeleted;

    private String resetToken;

    private LocalDateTime resetTokenExpiry;
    @OneToMany(mappedBy = "user")
    private List<GatheringUser> gatheringUserList = new ArrayList<>();

    @Builder
    public User(Long id, String userCode, String userName, String email, String password, String role, String profileImage, String provider, String oauthId, List<GatheringUser> gatheringUserList) {
        this.id = id;
        this.userCode = userCode;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.profileImage = profileImage;
        this.provider = provider;
        this.oauthId = oauthId;
        this.isDeleted = false;
        this.resetToken = null;
        this.resetTokenExpiry = null;
        this.gatheringUserList = gatheringUserList != null ? gatheringUserList : new ArrayList<>();
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public void deactivate() {
        this.isDeleted = true;
    }

    public void reactivate(String oauthId, String provider) {
        this.isDeleted = false;
        this.oauthId = oauthId;
        this.provider = provider;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void resetPassword(String newPassword) {
        this.password = newPassword;
        this.resetToken = null;
        this.resetTokenExpiry = null;
    }
    public UserResponse toDto() {
        UserResponse userDto = new UserResponse();
        userDto.setUserId(this.getId());
        userDto.setUserCode(this.getUserCode());
        userDto.setUserName(this.getUserName());
        userDto.setProfileImage(this.getProfileImage());
        return userDto;
    }
}
