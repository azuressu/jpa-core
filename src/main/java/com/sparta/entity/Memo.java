package com.sparta.entity;

import jakarta.persistence.*;

@Entity               // JPA가 관리할 수 있는 Entity 클래스 지정 (엔티티 클래스 명이 이름이 됨)
@Table(name = "memo") // 매핑할 테이블의 이름을 지정
public class Memo {
    // 반드시 기본 생성자가 있는지 확인해 주어야 함 !

    // 기본 키
    // 엔티티 구분 시 사용 - 식별자 역할
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)    // AUTO_INCREMENT 값이 자동 증가함 ..! - 다만 test 때문에 뺄 것
    private Long id;

    // nullable: null값 허용 여부 (defalut는 true임)
    // unique: 중복 허용 여부 (false 일때 중복 허용) / true라면 중복값 입력 불가능
    // 필드와 매핑할 테이블의 컬럼 설정 가능
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // length: 컬럼 길이 지정 (기본 값은 255)
    @Column(name = "contents", nullable = false, length = 500)
    private String contents;

    // 1. 테이블이 없는 상태에서 매핑할 때
    // 2. 테이블이 있는 상태에서 매핑할 때 - 존재하는 테이블 상황에 맞춰서 매핑해주어야 함

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}