package cart.service;

import cart.domain.member.Member;
import cart.dto.member.MemberDto;
import cart.dto.member.MemberRequestDto;
import cart.entity.MemberEntity;
import cart.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public MemberDto join(MemberRequestDto requestDto) {
        Member member = new Member(requestDto.getEmail(), requestDto.getPassword());
        MemberEntity entity = new MemberEntity(null, member.getEmail(), member.getPassword());
        MemberEntity savedEntity = repository.save(entity);
        return MemberDto.fromEntity(savedEntity);
    }

    public MemberDto findById(Long id) {
        Optional<MemberEntity> nullableEntity = repository.findById(id);
        if (nullableEntity.isEmpty()) {
            throw new IllegalArgumentException("id와 일치하는 회원이 없습니다.");
        }
        MemberEntity foundEntity = nullableEntity.get();
        return MemberDto.fromEntity(foundEntity);
    }

    public MemberDto findByEmail(String email) {
        Optional<MemberEntity> nullableEntity = repository.findByEmail(email);
        if (nullableEntity.isEmpty()) {
            throw new IllegalArgumentException("email과 일치하는 회원이 없습니다.");
        }
        MemberEntity foundEntity = nullableEntity.get();
        return MemberDto.fromEntity(foundEntity);
    }

    public List<MemberDto> findAll() {
        List<MemberEntity> entities = repository.findAll();
        return entities.stream()
                .map(MemberDto::fromEntity)
                .collect(toList());
    }

    public MemberDto updateById(MemberRequestDto requestDto, Long id) {
        Member member = new Member(requestDto.getEmail(), requestDto.getPassword());
        MemberEntity entity = new MemberEntity(id, member.getEmail(), member.getPassword());
        repository.update(entity);
        return MemberDto.fromEntity(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
