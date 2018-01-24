package ee.yals.models.dao;

import ee.yals.models.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LinkDao {

    @Autowired
    private LinkRepo linkRepo;

    public Optional<Link> findSingleByIdent(String linkIdent) {
        return linkRepo.findSingleByIdent(linkIdent);
    }

    public Link save(Link linkToSave) {
        return linkRepo.save(linkToSave);
    }

    public long count() {
        return linkRepo.count();
    }

}
