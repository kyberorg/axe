package ee.yals.controllers;

import ee.yals.utils.GitInfo;
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
 * @since 1.0
 */
@Controller
public class Front {

    @Autowired
    private GitInfo gitInfo;

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

        String latestCommit = gitInfo.getLatestCommitHash();
        String latestTag = gitInfo.getLatestTag();

        boolean displayCommitInfo = StringUtils.isNoneBlank(latestCommit, latestTag);

        params.addAttribute("displayCommitInfo", displayCommitInfo);
        params.addAttribute("commitHash", latestCommit);
        params.addAttribute("commit", latestCommit.substring(0, Integer.min(latestCommit.length(), 7)));
        params.addAttribute("commitTag", latestTag);
        params.addAttribute("repository", GitInfo.REPOSITORY);

        return "index";
    }

}
