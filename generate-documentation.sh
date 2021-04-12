#!/bin/bash
echo ""
echo "=================== Generate Documentation ==================="
REMOTE_URL=$(git config --get remote.origin.url)
PROJECT_DIR="$(pwd)"
###############################################################################################################
###############################################################################################################
generate_documentation() {
    ./mvnw --batch-mode clean package
    EXIT_CODE=$?
    if [[ ${EXIT_CODE} -gt 0 ]]; then
        echo "========== Documentation generation failed =========="
        exit ${EXIT_CODE}
    fi
    echo "========== Documentation generated =========="
}
delete_files(){
    echo "Delete visible file"
    for file in *; do
        rm -rf "$file"
    done
    echo "Delete hidden file"
    for file in .*; do
        if [[ "${file}" != ".git" ]]; then
            rm -rf "$file"
        fi
    done
}
checkout_documentation_branch() {
    echo "========== Initialize documentation branch =========="
    git clone "${REMOTE_URL}" ../../documentation
    cd ../../documentation || exit
    DOC_BRANCH_EXIST=$(git ls-remote --heads "${REMOTE_URL}" gh-pages | wc -l)
    if [[ ${DOC_BRANCH_EXIST} -eq 0 ]]; then
        echo "No documentation branch found creating one"
        git checkout -b gh-pages
        EXIT_CODE=$?
        if [[ ${EXIT_CODE} -gt 0 ]]; then
            echo "Unable to create Documentation pages"
            exit ${EXIT_CODE}
        fi
        delete_files
        git add .
        git commit -a -m "Clean documentation branch"
        git push origin gh-pages
    else
        echo "Documentation branch found clean it"
        git checkout gh-pages
        delete_files
    fi
}
copy_documentation() {
    echo "========== Copy documentation =========="
    cp -r "${PROJECT_DIR}/target/generated-docs/." "./"
}
push_documentation() {
    git add .
    git commit -a -m "Update Documentation"
    git pull origin gh-pages
    git push origin gh-pages
}
###############################################################################################################
###############################################################################################################
generate_documentation
checkout_documentation_branch
copy_documentation
push_documentation