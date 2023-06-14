import com.sparta.entity.Memo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EntityStateTest {

    EntityManagerFactory emf;
    EntityManager em;

    @BeforeEach
        // test 메서드가 5개가 있고, 5개를 실행한다면, entityfactory와 manager를 각각 만들기 때문
    void setUp() {
        // 여기서 주는 NAME이 persistence.xml 파일 내부의 persistence-unit 값임
        emf = Persistence.createEntityManagerFactory("memo");
        // 매니저 생성
        em = emf.createEntityManager();
    }

    @Test
    @DisplayName("비영속과 영속 상태")
    void test1() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = new Memo(); // 비영속 상태
            memo.setId(1L);
            memo.setUsername("Robbie");
            memo.setContents("비영속과 영속 상태");

            em.persist(memo);   // 영속성 컨텍스트에 저장함

            et.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test1() - 비영속과 영속 상태

    @Test
    @DisplayName("준영속 상태 : detach()")
    void test2() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = em.find(Memo.class, 1);
            System.out.println("memo.getId() = " + memo.getId());
            System.out.println("memo.getUsername() = " + memo.getUsername());
            System.out.println("memo.getContents() = " + memo.getContents());

            // em.contains(entity) : Entity 객체가 현재 영속성 컨텍스트에 저장되어 관리되는 상태인지 확인하는 메서드
            System.out.println("em.contains(memo) = " + em.contains(memo)); // true - 관리되고 있는 상태

            System.out.println("detach() 호출");
            em.detach(memo);
            System.out.println("em.contains(memo) = " + em.contains(memo)); // false - 관리되지 않는 상태

            System.out.println("memo Entity 객체 수정 시도");
            memo.setUsername("Update");
            memo.setContents("memo Entity Update");
            // 영속성 컨텍스트로 관리되고 있는 영속 상태 MANAGED 상태일 때만
            // 변경 감지가 체킹 됨. Dirty Checking이 진행됨. 수정이 자동으로 이루어짐

            System.out.println("트랜잭션 commit 전");
            et.commit();
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test2() - 준영속 상태

    @Test
    @DisplayName("준영속 상태 : clear()")
    void test3() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo1 = em.find(Memo.class, 1);
            Memo memo2 = em.find(Memo.class, 2);

            // em.contains(entity) : Entity 객체가 현재 영속성 컨텍스트에 저장되어 관리되는 상태인지 확인하는 메서드
            System.out.println("em.contains(memo1) = " + em.contains(memo1));
            System.out.println("em.contains(memo2) = " + em.contains(memo2));

            System.out.println("clear() 호출");
            em.clear();
            System.out.println("em.contains(memo1) = " + em.contains(memo1));
            System.out.println("em.contains(memo2) = " + em.contains(memo2));

            System.out.println("memo#1 Entity 다시 조회");
            Memo memo = em.find(Memo.class, 1);
            System.out.println("em.contains(memo) = " + em.contains(memo));
            System.out.println("\n memo Entity 수정 시도");
            memo.setUsername("Update");
            memo.setContents("memo Entity Update");

            System.out.println("트랜잭션 commit 전");
            et.commit();
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test3()-clear

    @Test
    @DisplayName("준영속 상태 : close()")
    void test4() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo1 = em.find(Memo.class, 1);
            Memo memo2 = em.find(Memo.class, 2);

            // em.contains(entity) : Entity 객체가 현재 영속성 컨텍스트에 저장되어 관리되는 상태인지 확인하는 메서드
            System.out.println("em.contains(memo1) = " + em.contains(memo1));
            System.out.println("em.contains(memo2) = " + em.contains(memo2));

            System.out.println("close() 호출");
            em.close();
            Memo memo = em.find(Memo.class, 2); // Session/EntityManager is closed 메시지와 함께 오류 발생
            System.out.println("memo.getId() = " + memo.getId());

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test4() - close()

    @Test
    @DisplayName("merge() : 저장")
    void test5() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = new Memo();
            memo.setId(3L);
            memo.setUsername("merge()");
            memo.setContents("merge() 저장");

            System.out.println("merge() 호출");
            Memo mergedMemo = em.merge(memo);

            System.out.println("em.contains(memo) = " + em.contains(memo));
            System.out.println("em.contains(mergedMemo) = " + em.contains(mergedMemo));

            System.out.println("트랜잭션 commit 전");
            et.commit();
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test5() - merge

    @Test
    @DisplayName("merge() : 수정")  // merge: 수정 혹은 저장이 이루어짐
    void test6() {
        // 데이터베이스에서 조회 해온 후, 그것으로 영속성 상태로 만들고
        // 준영속 상태의 메모를 받아온 파라미터 값을 사용해서 병합
        // 결국 병합하는 용도로만 사용한 것
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = em.find(Memo.class, 3);
            System.out.println("memo.getId() = " + memo.getId());
            System.out.println("memo.getUsername() = " + memo.getUsername());
            System.out.println("memo.getContents() = " + memo.getContents());

            System.out.println("em.contains(memo) = " + em.contains(memo));

            System.out.println("detach() 호출");
            em.detach(memo); // 준영속 상태로 전환
            System.out.println("em.contains(memo) = " + em.contains(memo));

            System.out.println("준영속 memo 값 수정");
            memo.setContents("merge() 수정");

            System.out.println("\n merge() 호출");
            Memo mergedMemo = em.merge(memo);          // 엑셀의 병합
            System.out.println("mergedMemo.getContents() = " + mergedMemo.getContents());

            System.out.println("em.contains(memo) = " + em.contains(memo));
            System.out.println("em.contains(mergedMemo) = " + em.contains(mergedMemo));

            System.out.println("트랜잭션 commit 전");
            et.commit();  // update문 실행
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test6() - merge 수정
}
