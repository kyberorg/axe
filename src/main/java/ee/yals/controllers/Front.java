package ee.yals.controllers;

import ee.yals.GitInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

/**
 * Serves requests to index (/) page
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
@Controller
public class Front {

    @Autowired
    private GitInfo gitInfo;

    /**
     * Index (/) page
     * @return The index (FTL)
     */
    @RequestMapping("/")
    public String index(@ModelAttribute("varBox") ModelMap varBox) {
        if (Objects.isNull(gitInfo)) {
            return "index";
        }

        String latestCommit = gitInfo.getLatestCommitHash();
        String latestTag = gitInfo.getLatestTag();

        boolean displayCommitInfo = StringUtils.isNoneBlank(latestCommit, latestTag);

        varBox.addAttribute("displayCommitInfo", displayCommitInfo);
        varBox.addAttribute("commitHash", latestCommit);
        varBox.addAttribute("commit", latestCommit.substring(0, Integer.min(latestCommit.length(), 7)));
        varBox.addAttribute("commitTag", latestTag);
        varBox.addAttribute("repository", GitInfo.REPOSITORY);

        return "index";
    }

}
