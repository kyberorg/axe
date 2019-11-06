package eu.yals.controllers;

import eu.yals.constants.App;
import eu.yals.services.GitService;
import eu.yals.services.overall.OverallService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Serves requests to index (/) page
 *
 * @since 1.0
 */
@Slf4j
@Controller
public class Front {
    private static final String TAG = "[Front Page]";

    private final OverallService overallService;
    private final GitService gitService;

    public Front(@Qualifier("dbOverallService") OverallService overallService, GitService gitService) {
        this.overallService = overallService;
        this.gitService = gitService;
    }

    /**
     * Index (/) page
     *
     * @param params {@link java.util.Map}, which passes params ot FTL templates.
     *               In template they are reachable using 'params' name (set as annotation value)
     * @return Template (FTL) name
     */
    @RequestMapping("/")
    public String index(@ModelAttribute("params") ModelMap params) {
        String latestCommit = gitService.getGitInfoSource().getLatestCommitHash().trim();
        String latestTag = gitService.getGitInfoSource().getLatestTag().trim();

        boolean commitPresent = (!latestCommit.equals(App.NO_VALUE) && StringUtils.isNotBlank(latestCommit));
        boolean tagPresent = (!latestTag.equals(App.NO_VALUE) && StringUtils.isNotBlank(latestTag));

        boolean displayCommitInfo = commitPresent && tagPresent;
        log.trace("{} will I display footer: {}. Commit present: {}. Tag present: {} ",
                TAG, displayCommitInfo, commitPresent, tagPresent);

        params.addAttribute("displayCommitInfo", displayCommitInfo);
        params.addAttribute("commitHash", latestCommit);
        params.addAttribute("commit", latestCommit.substring(0, Integer.min(latestCommit.length(), 7)));
        params.addAttribute("commitTag", latestTag);
        params.addAttribute("repository", App.Git.REPOSITORY);

        log.trace("{} Yals saved {} links", TAG, overallService.numberOfStoredLinks());
        params.addAttribute("overallLinks", overallService.numberOfStoredLinks());

        return "idx";
    }
}
