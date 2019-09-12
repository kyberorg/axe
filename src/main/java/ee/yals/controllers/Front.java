package ee.yals.controllers;

import ee.yals.services.overall.OverallService;
import ee.yals.utils.git.GitInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

/**
 * Serves requests to index (/) page
 *
 * @since 1.0
 */
@Controller
public class Front {

    private GitInfo gitInfo = GitInfo.getInstance();

    @Qualifier("dbOverallService")
    @Autowired
    private OverallService overallService;

    /**
     * Index (/) page
     *
     * @param params {@link java.util.Map}, which passes params ot FTL templates.
     *                                    In template they are reachable using 'params' name (set as annotation value)
     * @return Template (FTL) name
     */
    @RequestMapping("/")
    public String index(@ModelAttribute("params") ModelMap params) {
        if (Objects.isNull(gitInfo)) {
            return "index";
        }

        String latestCommit = gitInfo.getLatestCommitHash().trim();
        String latestTag = gitInfo.getLatestTag().trim();

        boolean commitPresent = (!latestCommit.equals(GitInfo.NOTHING_FOUND_MARKER) && StringUtils.isNotBlank(latestCommit));
        boolean tagPresent = (!latestTag.equals(GitInfo.NOTHING_FOUND_MARKER) && StringUtils.isNotBlank(latestTag));

        boolean displayCommitInfo = commitPresent && tagPresent;

        params.addAttribute("displayCommitInfo", displayCommitInfo);
        params.addAttribute("commitHash", latestCommit);
        params.addAttribute("commit", latestCommit.substring(0, Integer.min(latestCommit.length(), 7)));
        params.addAttribute("commitTag", latestTag);
        params.addAttribute("repository", GitInfo.REPOSITORY);

        params.addAttribute("overallLinks", overallService.numberOfStoredLinks());

        return "index";
    }
}
